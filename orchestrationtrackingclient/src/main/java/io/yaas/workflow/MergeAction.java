package io.yaas.workflow;

import io.yaas.workflow.Workflow.Action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by i303874 on 3/11/15.
 */
class MergeAction extends Action {
	
	private AtomicInteger count;

    private List<ActionResult> results = new CopyOnWriteArrayList<>();

    public MergeAction(int count) {
        super("merge", "1.0");
        
        this.count = new AtomicInteger(count);
        this.setFunction((action, arguments, result) -> {
            results.add(new ActionResult(this, arguments));

            if (this.count.decrementAndGet() == 0) {
                // TODO make nicer !!!
                Map<String, Object> consolidatedArguments = new HashMap<>();
                results.stream().forEach((argument) -> {
                    consolidatedArguments.putAll(argument.getResult());
                });

                result.set(new ActionResult(action, new Arguments(consolidatedArguments)));
            }
            return result;
        });
    }
}