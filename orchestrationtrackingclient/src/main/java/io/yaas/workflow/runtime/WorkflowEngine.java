package io.yaas.workflow.runtime;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.SettableFuture;
import io.yaas.workflow.Action;
import io.yaas.workflow.ActionResult;
import io.yaas.workflow.Arguments;
import io.yaas.workflow.Workflow;
import io.yaas.workflow.runtime.tracker.client.WorkflowTrackingClient;
import io.yaas.workflow.runtime.tracker.model.ActionBean;
import io.yaas.workflow.runtime.tracker.model.State;
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

    public void runWorkflow(Workflow w, Arguments arguments) {
        String wid = onWorkflowStart(w);
        runAction(wid, w.getStartAction(), arguments);
    }

    private void runAction(String wid, Action action, Arguments arguments) {
        ActionInstance actionInstance = onActionStart(wid, action);

        // TODO factory
        ActionExecutor executor = getExecutor(actionInstance);
        SettableFuture<ActionResult> future = SettableFuture.create();
        Futures.addCallback(future, new FutureCallback<ActionResult>() {
            @Override
            public void onSuccess(ActionResult result) {
                onActionSuccess(actionInstance);
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
            executor.execute(arguments, future);
        } catch (Exception e) {
            onActionError(actionInstance, e);
        }
    }

    private ActionExecutor getExecutor(ActionInstance actionInstance) {
        return ActionExecutor.create(actionInstance);
    }

    private String onWorkflowStart(Workflow w) {
        System.out.println(w.getName() + " - started");
        WorkflowBean bean = _trackingClient.createWorkflow(new WorkflowBean(w));
        return bean.wid;
    }

    private void onWorkflowSuccess(Workflow w) {

        System.out.println(w.getName() + " - succeeded");
    }

    private void onWorkflowFailure(Workflow w, Throwable error) {

        System.out.println(w.getName() + " - failed");
    }

    private void onWorkflowError(Workflow w, Throwable error) {

        System.out.println(w.getName() + " - error");
    }

    private ActionInstance onActionStart(String wid, Action action) {
        WorkflowBean workflowBean = new WorkflowBean();
        workflowBean.wid = wid;

        ActionBean actionBean = _trackingClient.createAction(new ActionBean(workflowBean, action));
        System.out.println(action.getName() + " - started");
        return new ActionInstance(wid, actionBean.aid, actionBean.timestamp, action);
    }

    private void onActionSuccess(ActionInstance actionInstance) {
        ActionBean actionBean = new ActionBean(actionInstance);
        actionBean.astate = State.SUCCEEDED;
        _trackingClient.updateAction(actionBean);
        System.out.println(actionInstance.getAction().getName() + " - succeeded");
    }

    // application error
    private void onActionError(ActionInstance actionInstance, Throwable error) {
        ActionBean actionBean = new ActionBean(actionInstance);
        actionBean.astate = State.FAILED;
        _trackingClient.updateAction(actionBean);
        System.out.println(actionBean.name + " - error");

        // TODO when to call onUnknown?
        System.out.println(actionBean.name + " - failed: " + error.getClass() + ": " + error.getMessage());
        error.printStackTrace();
    }

    // technical error
    private void onActionFailure(ActionResult actionResult, Throwable error) {
//        actionResult.getActionBean().astate = State.FAILED;
//        _trackingClient.updateAction(actionResult.getActionBean());
//        System.out.println(actionResult.getAction().getName() + " - failure");

//        System.out.println(actionResult.getAction().getName() + " - failed: " + error.getClass() + ": " + error.getMessage());
        error.printStackTrace();
    }
}
