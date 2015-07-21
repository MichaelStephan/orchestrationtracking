package io.yaas.workflow.runtime.action.instance;

import com.google.common.util.concurrent.SettableFuture;
import io.yaas.workflow.action.Action;
import io.yaas.workflow.action.ActionResult;
import io.yaas.workflow.action.Arguments;
import io.yaas.workflow.runtime.WorkflowInstance;

/**
 * Created by i303874 on 4/29/15.
 */
public class EndActionInstance extends SimpleActionInstance {

    public EndActionInstance(String id, Action action) {
        super(id, action);
    }

    @Override
    public void succeed(WorkflowInstance workflowInstance, ActionResult result) {
        super.succeed(workflowInstance, result);
        workflowInstance.succeed();
        workflowInstance.getResultHandler().succeeded(workflowInstance, result.getResult());
    }

    @Override
    public void execute(WorkflowInstance workflowInstance, Arguments arguments, SettableFuture<ActionResult> result) throws Exception {
        result.set(new ActionResult(arguments));
    }

    @Override
    public ActionResult restore(WorkflowInstance workflowInstance) {
        return ActionResult.EMPTY_RESULT;
    }
}
