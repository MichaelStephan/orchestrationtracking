package io.yaas.workflow.runtime;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.SettableFuture;
import io.yaas.workflow.Workflow;
import io.yaas.workflow.action.ActionResult;
import io.yaas.workflow.action.Arguments;
import io.yaas.workflow.runtime.action.instance.EndActionInstance;
import io.yaas.workflow.runtime.action.instance.WorkflowInstance;
import io.yaas.workflow.runtime.execution.ExecutionStrategy;
import io.yaas.workflow.runtime.execution.ForcedCompensationExecutor;
import io.yaas.workflow.runtime.execution.StandardExecutor;
import io.yaas.workflow.runtime.tracker.client.WorkflowTrackingClient;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by i303874 on 3/11/15.
 */
public class WorkflowEngine {

    private WorkflowTrackingClient _trackingClient;

    public WorkflowEngine(String trackingEndpoint) {
        checkNotNull(trackingEndpoint);
        this._trackingClient = new WorkflowTrackingClient(trackingEndpoint);
    }

    private ActionInstance findEndActionInstance(ActionInstance action) {
        checkNotNull(action);

        System.out.println(action.getClass());

        if (action instanceof EndActionInstance) {
            return action;
        } else {
            return action.getSuccessors().stream().map(successor -> findEndActionInstance(successor)).findAny().get();
        }
    }

    public void runWorkflow(Workflow workflow, ActionInstance startAction, Arguments arguments) {
        WorkflowInstance workflowInstance = new WorkflowInstance(workflow, _trackingClient, startAction, findEndActionInstance(startAction));
        runAction(StandardExecutor.getInstance(), workflowInstance, startAction, arguments);
    }

    public void compensateWorkflow(Workflow workflow, String wid, ActionInstance startAction) {
        WorkflowInstance workflowInstance = new WorkflowInstance(workflow, _trackingClient, startAction, findEndActionInstance(startAction), wid);

        runAction(new ForcedCompensationExecutor(), workflowInstance, workflowInstance.getEnd(), Arguments.EMPTY_ARGUMENTS);
    }

    private void runAction(ExecutionStrategy executor, WorkflowInstance workflow, ActionInstance action, Arguments arguments) {
        SettableFuture<ActionResult> future = SettableFuture.create();
        Futures.addCallback(future, new FutureCallback<ActionResult>() {
            @Override
            public void onSuccess(ActionResult result) {
                executor.success(workflow, result, action);

                executor.getNext(action).forEach((next) -> {
                    runAction(executor, workflow, next, result.getResult());
                });
            }

            @Override
            public void onFailure(Throwable cause) {
                // TODO leads to infinite loop in case of fail fast fails

                executor.error(workflow, action, arguments, cause);
                arguments.addError(cause);

                ExecutionStrategy fallbackExecutor = executor.getActionErrorStrategy();
                if (fallbackExecutor != null) {
                    runAction(fallbackExecutor, workflow, workflow.getEnd(), arguments);
                }
            }
        });

        executor.start(workflow, action);
        executor.execute(workflow, action, arguments, future);
    }
}
