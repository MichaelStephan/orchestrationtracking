package io.yaas.workflow.runtime;

import com.google.common.util.concurrent.SettableFuture;
import io.yaas.workflow.ActionResult;
import io.yaas.workflow.Arguments;

abstract class ActionExecutor {

    protected ActionInstance _actionInstance;

    static ActionExecutor create(ActionInstance actionInstance) {
        if (actionInstance.getAction().getPredecessors().size() > 1)
            return new MergeActionExecutor((MergeActionInstance)actionInstance);
        else
            return new SimpleActionExecutor(actionInstance);
    }

//    void execute(MergeActionInstance actionInstance, Arguments arguments, SettableFuture<ActionResult> result) {
//
//    }
//
//    void execute(StartActionInstance actionInstance, Arguments arguments, SettableFuture<ActionResult> result) {
//
//    }
//    void execute(EndActionInstance actionInstance, Arguments arguments, SettableFuture<ActionResult> result) {
//
//    }
//    void execute(SimpleActionInstance actionInstance, Arguments arguments, SettableFuture<ActionResult> result) {
//
//    }
    protected ActionExecutor(ActionInstance actionInstance) {
        _actionInstance = actionInstance;
    }

    abstract void execute(Arguments arguments, SettableFuture<ActionResult> result);
}
