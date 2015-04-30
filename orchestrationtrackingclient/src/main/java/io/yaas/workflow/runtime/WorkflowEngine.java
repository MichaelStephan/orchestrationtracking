package io.yaas.workflow.runtime;

import io.yaas.workflow.Arguments;
import io.yaas.workflow.Workflow;
import io.yaas.workflow.runtime.action.instance.WorkflowInstance;
import io.yaas.workflow.runtime.execution.StandardExecutor;
import io.yaas.workflow.runtime.tracker.client.WorkflowTrackingClient;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by i303874 on 3/11/15.
 */
public class WorkflowEngine {

    private WorkflowTrackingClient _trackingClient;
    private WorkflowProcessor processor;

    public WorkflowEngine(String trackingEndpoint) {
        checkNotNull(trackingEndpoint);
        this._trackingClient = new WorkflowTrackingClient(trackingEndpoint);
    }

    public void runWorkflow(Workflow workflow, ActionInstance startAction, Arguments arguments) {
        WorkflowInstance workflowInstance = new WorkflowInstance(workflow, _trackingClient);
        processor = new WorkflowProcessor(workflowInstance);
        runAction(workflowInstance, startAction, arguments);
    }

    private void runAction(WorkflowInstance workflow, ActionInstance action, Arguments arguments) {
        processor.process(StandardExecutor.getInstance(), action, arguments);


//        SettableFuture<ActionResult> future = SettableFuture.create();
//        Futures.addCallback(future, new FutureCallback<ActionResult>() {
//            @Override
//            public void onSuccess(ActionResult result) {
//                action.succeed(workflow, _trackingClient);
//
//                action.getSuccessors().forEach((successor) -> {
//                    runAction(workflow, successor, result.getResult());
//                });
//            }
//
//            @Override
//            public void onFailure(Throwable cause) {
//                action.error(workflow, _trackingClient, cause);
//
//                ActionErrorHandler errorHandler = action.getAction().getErrorHandler();
//                if (errorHandler == null) {
//                    errorHandler = new FailFastErrorHandler();
//                }
//                errorHandler.execute(workflow, action, arguments, cause);
//            }
//        });
//
//        action.start(workflow, _trackingClient);
//        action.execute(arguments, future);
    }
}
