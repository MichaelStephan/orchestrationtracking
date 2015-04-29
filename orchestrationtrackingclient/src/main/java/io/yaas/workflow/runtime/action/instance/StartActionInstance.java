package io.yaas.workflow.runtime.action.instance;

import com.google.common.util.concurrent.SettableFuture;
import io.yaas.workflow.Action;
import io.yaas.workflow.ActionResult;
import io.yaas.workflow.Arguments;
import io.yaas.workflow.runtime.ActionInstance;
import io.yaas.workflow.runtime.tracker.client.WorkflowTrackingClient;

/**
 * Created by i303874 on 4/29/15.
 */
public class StartActionInstance extends SimpleActionInstance {
    public StartActionInstance(String id, Action action) {
        super(id, action);
    }

    @Override
    public void execute(Arguments arguments, SettableFuture<ActionResult> result) {
        result.set(new ActionResult(arguments));
    }

    public void start(WorkflowInstance workflowInstance, WorkflowTrackingClient client) {
        workflowInstance.start();
    }

    public void succeed(WorkflowInstance workflowInstance, WorkflowTrackingClient client) {
    }

    public void error(WorkflowInstance workflowInstance, ActionInstance actionInstance, Arguments arguments, WorkflowTrackingClient client, Throwable cause) {
    }
}
