package io.yaas.workflow.runtime;

import com.google.common.util.concurrent.SettableFuture;
import io.yaas.workflow.ActionResult;
import io.yaas.workflow.Arguments;

class SimpleActionExecutor extends ActionExecutor {

    public SimpleActionExecutor(ActionInstance actionInstance) {
        super(actionInstance);
    }

    @Override
    public void execute(Arguments arguments, SettableFuture<ActionResult> result) {
        new Thread(() -> {
            result.set(_actionInstance.getAction().getFunction().apply(_actionInstance, arguments));
        }).start();
    }
}
