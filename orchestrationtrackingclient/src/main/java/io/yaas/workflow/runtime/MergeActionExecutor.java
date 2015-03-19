package io.yaas.workflow.runtime;

import io.yaas.workflow.Action;
import io.yaas.workflow.ActionResult;
import io.yaas.workflow.Arguments;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.common.util.concurrent.SettableFuture;

class MergeActionExecutor extends ActionExecutor {

	// TODO consider CountDownLatch instead
	private AtomicInteger _count;
    private List<ActionResult> _results = new CopyOnWriteArrayList<>();
    
    public MergeActionExecutor(Action action) {
    	super(action);
    	_count = new AtomicInteger(action.getPredecessors().size());
	}
    
	@Override
	public void execute(Arguments arguments, SettableFuture<ActionResult> result) {
        new Thread(() -> {
            _results.add(new ActionResult(_action, arguments));
            if (_count.decrementAndGet() == 0) {
                // TODO make nicer !!!
                Map<String, Object> consolidatedArguments = new HashMap<>();
                _results.stream().forEach((argument) -> {
                    consolidatedArguments.putAll(argument.getResult());
                });

                result.set(new ActionResult(_action, new Arguments(consolidatedArguments)));
            }
        }).start();
	}

}
