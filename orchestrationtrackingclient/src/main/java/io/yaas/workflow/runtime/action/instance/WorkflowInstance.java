package io.yaas.workflow.runtime.action.instance;

import io.yaas.workflow.Workflow;
import io.yaas.workflow.runtime.ActionInstance;
import io.yaas.workflow.runtime.tracker.client.WorkflowTrackingClient;
import io.yaas.workflow.runtime.tracker.model.State;
import io.yaas.workflow.runtime.tracker.model.WorkflowBean;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by i303874 on 4/29/15.
 */
public class WorkflowInstance {
    private Workflow workflow;

    private String id;

    public ActionInstance getStart() {
        return start;
    }

    public ActionInstance getEnd() {
        return end;
    }

    private ActionInstance start;

    private ActionInstance end;

    public WorkflowTrackingClient getTrackingClient() {
        return client;
    }

    private WorkflowTrackingClient client;

    public WorkflowInstance(Workflow workflow, WorkflowTrackingClient client, ActionInstance start, ActionInstance end) {
        this.workflow = checkNotNull(workflow);
        this.client = checkNotNull(client);
        this.start = checkNotNull(start);
        this.end = checkNotNull(end);
    }

    public Workflow getWorkflow() {
        return workflow;
    }

    public String getId() {
        if (id == null) {
            throw new IllegalStateException("workflow instance id not set");
        }
        return id;
    }

    public String getName() {
        return workflow.getName();
    }

    public int getVersion() {
        return workflow.getVersion();
    }

    public void start() {
        WorkflowBean bean = client.createWorkflow(new WorkflowBean(this.getName(), this.getVersion()));
        this.id = bean.wid;
    }

    public void succeed() {
        WorkflowBean bean = new WorkflowBean(getName(), getVersion());
        bean.wstate = State.SUCCEEDED;
        bean.wid = getId();
        client.updateWorkflow(bean);
        this.id = null;
    }

    public void error() {
        WorkflowBean bean = new WorkflowBean(getName(), getVersion());
        bean.wstate = State.FAILED;
        bean.wid = getId();
        client.updateWorkflow(bean);
        this.id = null;
    }
}
