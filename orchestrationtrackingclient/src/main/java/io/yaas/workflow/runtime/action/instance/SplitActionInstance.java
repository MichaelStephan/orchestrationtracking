package io.yaas.workflow.runtime.action.instance;

import com.google.common.util.concurrent.SettableFuture;
import io.yaas.workflow.action.ActionResult;
import io.yaas.workflow.action.Arguments;
import io.yaas.workflow.action.SplitAction;

import java.util.Collections;

/**
 * Created by i303874 on 4/29/15.
 */
public class SplitActionInstance extends SimpleActionInstance {

    public SplitActionInstance(String id, SplitAction action, int count) {
        super(id, action);
        this.compensationActionInstance = new SplitCompensationActionInstance(this, count);
    }

    @Override
    public void execute(WorkflowInstance workflowInstance, Arguments arguments, SettableFuture<ActionResult> result) {
        result.set(new ActionResult(arguments));
    }

    @Override
    public ActionResult restore(WorkflowInstance workflowInstance) {
        return new ActionResult(new Arguments(Collections.emptyMap()));
    }
}
