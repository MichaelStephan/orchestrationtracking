package io.yaas.workflow.runtime.action.instance;

import io.yaas.workflow.Workflow;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by i303874 on 4/29/15.
 */
public class WorkflowInstance {
    private Workflow workflow;

    private String id;

    public WorkflowInstance(Workflow workflow) {
        this.workflow = checkNotNull(workflow);
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

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return workflow.getName();
    }

    public int getVersion() {
        return workflow.getVersion();
    }
}
