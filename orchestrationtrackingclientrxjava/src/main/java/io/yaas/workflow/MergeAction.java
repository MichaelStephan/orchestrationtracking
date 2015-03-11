package io.yaas.workflow;

import com.google.common.collect.ImmutableMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by i303874 on 3/11/15.
 */
public class MergeAction extends Action {
    private AtomicInteger count;

    private List<ActionResult> results = new CopyOnWriteArrayList<>();

    public MergeAction(List<Action> successors, int count) {
        super(UUID.randomUUID().toString(), "merge", successors);

        this.count = new AtomicInteger(count);
        this.setFunc((action, arguments, result) -> {
            results.add(new ActionResult(this, arguments));

            if (this.count.decrementAndGet() == 0) {
                // TODO make nicer !!!
                Map<String, Object> consolidatedArguments = new HashMap<>();
                results.stream().forEach((argument) -> {
                    consolidatedArguments.putAll(argument.getResult());
                });

                result.set(new ActionResult(action, new ImmutableMap.Builder<String, Object>().putAll(consolidatedArguments).build()));
            }
            return result;
        });
    }
}