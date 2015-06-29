package io.yaas.workflow.runtime.action.instance;

import com.google.common.util.concurrent.SettableFuture;
import io.yaas.workflow.action.Action;
import io.yaas.workflow.action.ActionResult;
import io.yaas.workflow.action.Arguments;
import io.yaas.workflow.runtime.ActionInstance;
import io.yaas.workflow.runtime.tracker.model.ActionBean;
import io.yaas.workflow.runtime.tracker.model.ResultBean;
import io.yaas.workflow.runtime.tracker.model.State;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by i303874 on 4/29/15.
 */
public class SimpleActionInstance extends BaseActionInstance implements ActionInstance {
    private Action action;

    private CompensationActionInstance compensationActionInstance;

    protected String lastCreatedTimestamp;

    public SimpleActionInstance(String id, Action action) {
        this.id = checkNotNull(id);
        this.action = checkNotNull(action);
    }

    public String getId() {
        return (id + "_" + getName() + "_" + getVersion()).replaceAll("\\s", "");
    }

    @Override
    public String getName() {
        return this.action.getName();
    }

    @Override
    public String getVersion() {
        return this.action.getVersion();
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
    public void execute(WorkflowInstance workflowInstance, Arguments arguments, SettableFuture<ActionResult> result) {
        new Thread(() -> {
            try {
                result.set(action.getActionFunction().apply(arguments));
            } catch (Exception e) {
                result.setException(e);
            }
        }).start();
    }

    public String toString() {
        return action.toString();
    }

    @Override
    public Action getAction() {
        return action;
    }

    public Iterator<ActionInstance> iterator() {
        return getSuccessors().iterator();
    }

    public CompensationActionInstance getCompensationActionInstance() {
        return compensationActionInstance;
    }

    public void setCompensationActionInstance(CompensationActionInstance compensationActionInstance) {
        this.compensationActionInstance = compensationActionInstance;
    }

    @Override
    public ActionResult restore(WorkflowInstance workflowInstance) {
        ActionBean bean = new ActionBean();
        bean.wid = workflowInstance.getId();
        bean.aid = getId();
        bean.timestamp = lastCreatedTimestamp;

        ResultBean result = workflowInstance.getTrackingClient().getActionData(bean);
        return new ActionResult(new Arguments(result.result));
    }


}

