package io.yaas.workflow.runtime;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.SettableFuture;
import io.yaas.workflow.ActionResult;
import io.yaas.workflow.Arguments;
import io.yaas.workflow.Workflow;
import io.yaas.workflow.runtime.tracker.client.WorkflowTrackingClient;
import io.yaas.workflow.runtime.tracker.model.WorkflowBean;

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


    private String onWorkflowStart(Workflow w) {
        System.out.println(w.getName() + " - started");
        WorkflowBean bean = _trackingClient.createWorkflow(new WorkflowBean(w));
        return bean.wid;
    }

    public void runWorkflow(Workflow w, ActionInstance startActionInstance, Arguments arguments) {
        String wid = onWorkflowStart(w);
        runAction(wid, startActionInstance, arguments);
    }

    private void runAction(String wid, ActionInstance action, Arguments arguments) {

        action.start(wid, _trackingClient);

        SettableFuture<ActionResult> future = SettableFuture.create();
        Futures.addCallback(future, new FutureCallback<ActionResult>() {
            @Override
            public void onSuccess(ActionResult result) {
                action.succeed(wid, _trackingClient);

                action.getSuccessors().forEach((successor) -> {
                    runAction(wid, successor, result.getResult());
                });
            }

            @Override
            public void onFailure(Throwable error) {
                System.out.println("TODO fix onActionFailure");
                // TODO onActionFailure(action, error);
            }
        });

        try {
            action.execute(arguments, future);
        } catch (Exception e) {
            action.error(wid, _trackingClient, e);
        }
    }
}
