package io.yaas.workflow.runtime.action.instance;

import com.google.common.util.concurrent.SettableFuture;
import io.yaas.workflow.Action;
import io.yaas.workflow.ActionResult;
import io.yaas.workflow.Arguments;
import io.yaas.workflow.runtime.ActionInstance;
import io.yaas.workflow.runtime.tracker.client.WorkflowTrackingClient;
import io.yaas.workflow.runtime.tracker.model.ActionBean;
import io.yaas.workflow.runtime.tracker.model.State;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by i303874 on 4/29/15.
 */
public class SimpleActionInstance implements ActionInstance {
    private Action action;

    private Set<ActionInstance> successors = new HashSet<>();

    private Set<ActionInstance> predecessors = new HashSet<>();

    private String id;

    private String lastCreatedTimestamp;

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
    public void start(WorkflowInstance workflowInstance, WorkflowTrackingClient client) {
        this.lastCreatedTimestamp = client.createAction(new ActionBean(workflowInstance.getId(), getName(), getVersion(), getId())).timestamp;
    }

    @Override
    public void succeed(WorkflowInstance workflowInstance, WorkflowTrackingClient client) {
        ActionBean actionBean = new ActionBean(workflowInstance.getId(), getName(), getVersion(), getId(), lastCreatedTimestamp);
        actionBean.astate = State.SUCCEEDED;
        client.updateAction(actionBean);
    }

    @Override
    public void error(WorkflowInstance workflowInstance, WorkflowTrackingClient client, Throwable cause) {
        ActionBean actionBean = new ActionBean(workflowInstance.getId(), getName(), getVersion(), getId(), lastCreatedTimestamp);
        actionBean.astate = State.FAILED;
        client.updateAction(actionBean);
    }

    @Override
    public void execute(Arguments arguments, SettableFuture<ActionResult> result) {
        new Thread(() -> {
            result.set(action.getFunction().apply(this, arguments));
        }).start();
    }

    public String toString() {
        return action.toString();
    }

    public void addSuccessor(ActionInstance action) {
        successors.add(checkNotNull(action));
    }

    @Override
    public void addPredecessor(ActionInstance action) {
        predecessors.add(checkNotNull(action));
    }

    @Override
    public Collection<ActionInstance> getSuccessors() {
        return this.successors;
    }

    @Override
    public Collection<ActionInstance> getPredecessors() {
        return this.predecessors;
    }
}
