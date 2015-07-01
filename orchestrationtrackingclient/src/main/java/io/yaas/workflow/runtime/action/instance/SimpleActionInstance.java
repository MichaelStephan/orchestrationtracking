package io.yaas.workflow.runtime.action.instance;

import com.google.common.util.concurrent.SettableFuture;
import io.yaas.workflow.action.Action;
import io.yaas.workflow.action.ActionResult;
import io.yaas.workflow.action.Arguments;
import io.yaas.workflow.runtime.ActionInstance;
import io.yaas.workflow.runtime.tracker.model.ActionBean;
import io.yaas.workflow.runtime.tracker.model.ResultBean;
import io.yaas.workflow.runtime.tracker.model.State;

/**
 * Created by i303874 on 4/29/15.
 */
public class SimpleActionInstance extends BaseActionInstance implements ActionInstance {
    private SimpleCompensationActionInstance compensationActionInstance = null;

    private Object compensationActionInstanceLock = new Object();

    protected String lastCreatedTimestamp;

    public SimpleActionInstance(String id, Action action) {
        super(id, action);
    }

    public String getId() {
        return (super.getId() + "_" + getName() + "_" + getVersion()).replaceAll("\\s", "");
    }

    @Override
    public ActionInstance getCompensationActionInstance() {
        synchronized (compensationActionInstanceLock) {
            if (compensationActionInstance == null) {
                compensationActionInstance = new SimpleCompensationActionInstance(this);
            }
            return compensationActionInstance;
        }
    }

    @Override
    public void start(WorkflowInstance workflowInstance) {
        this.lastCreatedTimestamp = workflowInstance.getTrackingClient().createAction(new ActionBean(workflowInstance.getId(), getName(), getVersion(), getId())).timestamp;
    }

    @Override
    public void succeed(WorkflowInstance workflowInstance, ActionResult result) {
        ActionBean actionBean = new ActionBean(workflowInstance.getId(), getName(), getVersion(), getId(), lastCreatedTimestamp);
        actionBean.astate = State.SUCCEEDED;
        actionBean.data = result.getResult();
        workflowInstance.getTrackingClient().updateAction(actionBean);
    }

    @Override
    public void error(WorkflowInstance workflowInstance, Throwable cause) {
        ActionBean actionBean = new ActionBean(workflowInstance.getId(), getName(), getVersion(), getId(), lastCreatedTimestamp);
        actionBean.astate = State.FAILED;
        workflowInstance.getTrackingClient().updateAction(actionBean);
    }

    @Override
    public void execute(WorkflowInstance workflowInstance, Arguments arguments, SettableFuture<ActionResult> result) throws Exception {
        result.set(action.getActionFunction().apply(arguments));
    }

    @Override
    public ActionResult restore(WorkflowInstance workflowInstance) {
        // TODO
        ResultBean result = workflowInstance.getTrackingClient().getLastActionData(workflowInstance.getId(), getId());
        return new ActionResult(new Arguments(result != null ? result.result : Arguments.EMPTY_ARGUMENTS));
    }
}

