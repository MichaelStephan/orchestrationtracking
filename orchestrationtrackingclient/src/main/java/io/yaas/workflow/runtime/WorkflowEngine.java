package io.yaas.workflow.runtime;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.SettableFuture;
import io.yaas.workflow.ActionErrorHandler;
import io.yaas.workflow.ActionResult;
import io.yaas.workflow.Arguments;
import io.yaas.workflow.Workflow;
import io.yaas.workflow.runtime.action.instance.WorkflowInstance;
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
        runAction(new WorkflowInstance(workflow, _trackingClient), startAction, arguments);
    }

    private void runAction(WorkflowInstance workflow, ActionInstance action, Arguments arguments) {
        SettableFuture<ActionResult> future = SettableFuture.create();
        Futures.addCallback(future, new FutureCallback<ActionResult>() {
            @Override
            public void onSuccess(ActionResult result) {
                action.succeed(workflow, _trackingClient);

                action.getSuccessors().forEach((successor) -> {
                    runAction(workflow, successor, result.getResult());
                });
            }

            @Override
            public void onFailure(Throwable cause) {
                action.error(workflow, _trackingClient, cause);

                ActionErrorHandler errorHandler = action.getAction().getErrorHandler();
                if (errorHandler == null) {
                    errorHandler = new FailFastActionErrorHandler();
                }
                errorHandler.execute(workflow, action, arguments, cause);
            }
        });

        action.start(workflow, _trackingClient);
        action.execute(arguments, future);
    }
}
