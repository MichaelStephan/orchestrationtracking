package io.yaas.workflow.runtime;

import io.yaas.workflow.Workflow;
import io.yaas.workflow.runtime.ActionInstance;
import io.yaas.workflow.runtime.WorkflowEngineResultHandler;
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

    private ActionInstance start;

    private ActionInstance end;

    private WorkflowTrackingClient client;

    private WorkflowEngineResultHandler resultHandler;

    public WorkflowInstance(Workflow workflow, WorkflowTrackingClient client, ActionInstance start, ActionInstance end, WorkflowEngineResultHandler resultHandler) {
        this.workflow = checkNotNull(workflow);
        this.client = checkNotNull(client);
        this.start = checkNotNull(start);
        this.end = checkNotNull(end);
        this.resultHandler = checkNotNull(resultHandler);
    }

    public WorkflowInstance(Workflow workflow, WorkflowTrackingClient client, ActionInstance start, ActionInstance end, String wid, WorkflowEngineResultHandler resultHandler) {
        this(workflow, client, start, end, resultHandler);
        this.id = checkNotNull(wid);
        this.resultHandler = checkNotNull(resultHandler);
    }

    public Workflow getWorkflow() {
        return workflow;
    }

    public ActionInstance getStart() {
        return start;
    }

    public ActionInstance getEnd() {
        return end;
    }

    public WorkflowEngineResultHandler getResultHandler() {
        return resultHandler;
    }

    public WorkflowTrackingClient getTrackingClient() {
        return client;
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
    }

    public void error() {
        WorkflowBean bean = new WorkflowBean(getName(), getVersion());
        bean.wstate = State.FAILED;
        bean.wid = getId();
        client.updateWorkflow(bean);
    }

    public void compensated() {
        WorkflowBean bean = new WorkflowBean(getName(), getVersion());
        bean.wstate = State.COMPENSATED;
        bean.wid = getId();
        client.updateWorkflow(bean);
    }
}
