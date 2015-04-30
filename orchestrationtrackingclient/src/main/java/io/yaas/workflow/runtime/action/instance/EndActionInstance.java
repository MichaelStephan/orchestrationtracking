package io.yaas.workflow.runtime.action.instance;

import com.google.common.util.concurrent.SettableFuture;
import io.yaas.workflow.Action;
import io.yaas.workflow.ActionResult;
import io.yaas.workflow.Arguments;
import io.yaas.workflow.runtime.tracker.client.WorkflowTrackingClient;

/**
 * Created by i303874 on 4/29/15.
 */
public class EndActionInstance extends SimpleActionInstance {
    public EndActionInstance(String id, Action action) {
        super(id, action);
    }

    @Override
    public void start(WorkflowInstance workflowInstance, WorkflowTrackingClient client) {

    }

    @Override
    public void succeed(WorkflowInstance workflowInstance, ActionResult result, WorkflowTrackingClient client) {
        workflowInstance.succeed();
    }

    @Override
    public void execute(Arguments arguments, SettableFuture<ActionResult> result) {
        result.set(new ActionResult(arguments));
    }
}
