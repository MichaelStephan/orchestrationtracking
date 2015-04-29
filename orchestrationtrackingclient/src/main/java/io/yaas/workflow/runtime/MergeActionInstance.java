package io.yaas.workflow.runtime;

import io.yaas.workflow.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by D032705 on 28.04.2015.
 */
public class MergeActionInstance extends ActionInstance {
    // TODO consider CountDownLatch instead
    private AtomicInteger _count;
    private List<ActionResult> _results = new CopyOnWriteArrayList<>();

    public MergeActionInstance(Workflow w) {
        super(new Action("Merge Action", "1.0", w));
        _count = new AtomicInteger(action.getPredecessors().size());
    }

    public int decrementAndGet() {
        return _count.incrementAndGet();
    }

    public void mergeResult(ActionResult result) {
        _results.add(result);
    }

    public ActionResult getResult() {
        Map<String, Object> consolidatedArguments = new HashMap<>();
        _results.stream().forEach((argument) -> {
            consolidatedArguments.putAll(argument.getResult());
        });
        return new ActionResult(this, new Arguments(consolidatedArguments));
    }
}
