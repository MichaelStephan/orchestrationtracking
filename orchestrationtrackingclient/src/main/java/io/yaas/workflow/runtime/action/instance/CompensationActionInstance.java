package io.yaas.workflow.runtime.action.instance;

import com.google.common.util.concurrent.SettableFuture;
import io.yaas.workflow.action.Action;
import io.yaas.workflow.action.ActionResult;
import io.yaas.workflow.action.Arguments;

/**
 * Created by D032705 on 13.05.2015.
 */
public class CompensationActionInstance extends SimpleActionInstance {
    public CompensationActionInstance(String id, Action action) {
        super(id + "_compensation", action);
    }


    @Override
    public void execute(WorkflowInstance workflowInstance, Arguments arguments, SettableFuture<ActionResult> result) {

    }
}
