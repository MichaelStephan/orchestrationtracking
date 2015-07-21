package io.yaas.workflow.runtime.action.instance;

import com.google.common.util.concurrent.SettableFuture;
import io.yaas.workflow.action.ActionResult;
import io.yaas.workflow.action.Arguments;
import io.yaas.workflow.runtime.ActionInstance;
import io.yaas.workflow.runtime.WorkflowInstance;
import io.yaas.workflow.runtime.tracker.model.ActionBean;
import io.yaas.workflow.runtime.tracker.model.State;

/**
 * Created by D032705 on 13.05.2015.
 */
public class SimpleCompensationActionInstance extends SimpleActionInstance {

    private ActionInstance actionInstance;

    public SimpleCompensationActionInstance(ActionInstance actionInstance) {
        super("compensation", actionInstance.getAction());
        this.actionInstance = actionInstance;
    }

    @Override
    public ActionInstance getCompensationActionInstance() {
        return null;
    }

    public ActionInstance getActionInstance() {
        return actionInstance;
    }

    public String getId() {
        return actionInstance.getId() + "_compensation";
    }

    @Override
    public void succeed(WorkflowInstance workflowInstance, ActionResult result) {
        ActionBean actionBean = new ActionBean(workflowInstance.getId(), getName(), getVersion(), getId(), lastCreatedTimestamp);
        actionBean.astate = State.SUCCEEDED;
        workflowInstance.getTrackingClient().updateAction(actionBean);
    }

    @Override
    public void execute(WorkflowInstance workflowInstance, Arguments arguments, SettableFuture<ActionResult> result) throws Exception {
        result.set(getAction().getCompensationFunction().apply(arguments));
    }
}
