package io.yaas.workflow.runtime.action.instance;

import com.google.common.util.concurrent.SettableFuture;
import io.yaas.workflow.Action;
import io.yaas.workflow.ActionResult;
import io.yaas.workflow.Arguments;
import io.yaas.workflow.runtime.tracker.client.WorkflowTrackingClient;
import io.yaas.workflow.runtime.tracker.model.WorkflowBean;

/**
 * Created by i303874 on 4/29/15.
 */
public class StartActionInstance extends SimpleActionInstance {
    public StartActionInstance(String id, Action action) {
        super(id, action);
    }

    @Override
    public void execute(WorkflowInstance workflowInstance, WorkflowTrackingClient client, Arguments arguments, SettableFuture<ActionResult> result) {
        result.set(new ActionResult(this, arguments));
    }

    public void start(WorkflowInstance workflowInstance, WorkflowTrackingClient client) {
        WorkflowBean bean = client.createWorkflow(new WorkflowBean(workflowInstance.getName(), workflowInstance.getVersion()));
        workflowInstance.setId(bean.wid);
    }

    public void succeed(WorkflowInstance workflowInstance, WorkflowTrackingClient client) {
    }

    public void error(WorkflowInstance workflowInstance, WorkflowTrackingClient client, Throwable cause) {
    }
}
