package io.yaas.workflow.runtime.action.instance;

import com.google.common.util.concurrent.SettableFuture;
import io.yaas.workflow.Action;
import io.yaas.workflow.ActionResult;
import io.yaas.workflow.Arguments;
import io.yaas.workflow.runtime.tracker.client.WorkflowTrackingClient;

import java.util.Collections;

/**
 * Created by i303874 on 4/29/15.
 */
public class StartActionInstance extends SimpleActionInstance {
    public StartActionInstance(String id, Action action) {
        super(id, action);
    }

    public void start(WorkflowInstance workflowInstance, WorkflowTrackingClient client) {
        workflowInstance.start();
        super.start(workflowInstance, client);
    }

    @Override
    public void execute(Arguments arguments, SettableFuture<ActionResult> result) {
        result.set(new ActionResult(arguments));
    }

    @Override
    public ActionResult restore(WorkflowInstance workflowInstance) {
        return new ActionResult(new Arguments(Collections.emptyMap()));
    }
}
