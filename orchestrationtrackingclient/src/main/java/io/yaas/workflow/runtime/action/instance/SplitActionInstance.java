package io.yaas.workflow.runtime.action.instance;

import com.google.common.util.concurrent.SettableFuture;
import io.yaas.workflow.ActionResult;
import io.yaas.workflow.Arguments;
import io.yaas.workflow.SplitAction;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by i303874 on 4/29/15.
 */
public class SplitActionInstance extends SimpleActionInstance {
    private AtomicInteger _count;
    private List<ActionResult> _results = new CopyOnWriteArrayList<>();

    public SplitActionInstance(String id, SplitAction action, int count) {
        super(id, action);
        this._count = new AtomicInteger(count);
    }

    @Override
    public void execute(Arguments arguments, SettableFuture<ActionResult> result) {
//        new Thread(() -> {
//            _results.add(new ActionResult(arguments));
//            if (_count.decrementAndGet() == 0) {
//                result.set(new ActionResult(arguments));
//            }
//        }).start();
        result.set(new ActionResult(arguments));
    }

    @Override
    public ActionResult restore(WorkflowInstance workflowInstance) {
        return new ActionResult(new Arguments(Collections.emptyMap()));
    }
}
