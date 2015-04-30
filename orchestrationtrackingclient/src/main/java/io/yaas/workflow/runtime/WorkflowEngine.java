package io.yaas.workflow.runtime;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.SettableFuture;
import io.yaas.workflow.ActionResult;
import io.yaas.workflow.Arguments;
import io.yaas.workflow.Workflow;
import io.yaas.workflow.runtime.action.instance.WorkflowInstance;
import io.yaas.workflow.runtime.execution.ExecutionStrategy;
import io.yaas.workflow.runtime.execution.RecoveryExecutor;
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

    public void runWorkflow(Workflow workflow, ActionInstance startAction, Arguments arguments) {
        WorkflowInstance workflowInstance = new WorkflowInstance(workflow, _trackingClient);
        runAction(StandardExecutor.getInstance(), workflowInstance, startAction, arguments);
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
                executor.error(workflow, action, arguments, cause);
                // throw new Exception(cause);
                arguments.addError(cause);
                runAction(RecoveryExecutor.getInstance(), workflow, action, arguments);
            }
        });
        executor.start(workflow, action);
        executor.execute(workflow, action, arguments, future);
    }
}
