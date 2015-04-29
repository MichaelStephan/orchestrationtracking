package io.yaas.workflow.runtime;

import com.google.common.util.concurrent.SettableFuture;
import io.yaas.workflow.ActionResult;
import io.yaas.workflow.Arguments;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

class MergeActionExecutor extends ActionExecutor {

    public MergeActionExecutor(MergeActionInstance actionInstance) {
        super(actionInstance);
    }

    @Override
    public void execute(Arguments arguments, SettableFuture<ActionResult> result) {
        new Thread(() -> {
            MergeActionInstance instance = (MergeActionInstance)_actionInstance;
            instance.mergeResult(new ActionResult(_actionInstance, arguments));
            if (instance.decrementAndGet() == 0) {
                result.set(instance.getResult());
            }
        }).start();
    }

}
