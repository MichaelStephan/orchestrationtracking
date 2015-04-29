package io.yaas.workflow.runtime.action.instance;

import com.google.common.util.concurrent.SettableFuture;
import io.yaas.workflow.ActionResult;
import io.yaas.workflow.Arguments;
import io.yaas.workflow.MergeAction;
import io.yaas.workflow.runtime.tracker.client.WorkflowTrackingClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by i303874 on 4/29/15.
 */
public class MergeActionInstance extends SimpleActionInstance {
    private AtomicInteger _count;
    private List<ActionResult> _results = new CopyOnWriteArrayList<>();

    public MergeActionInstance(String id, MergeAction action, int count) {
        super(id, action);
        this._count = new AtomicInteger(count);
    }

    @Override
    public void start(WorkflowInstance workflowInstance, WorkflowTrackingClient client) {

    }

    @Override
    public void succeed(WorkflowInstance workflowInstance, WorkflowTrackingClient client) {

    }

    @Override
    public void error(WorkflowInstance workflowInstance, WorkflowTrackingClient client, Throwable cause) {

    }

    @Override
    public void execute(Arguments arguments, SettableFuture<ActionResult> result) {
        new Thread(() -> {
            _results.add(new ActionResult(arguments));
            if (_count.decrementAndGet() == 0) {
                // TODO make nicer !!!
                Map<String, Object> consolidatedArguments = new HashMap<>();
                _results.stream().forEach((argument) -> {
                    consolidatedArguments.putAll(argument.getResult());
                });

                result.set(new ActionResult(new Arguments(consolidatedArguments)));
            }
        }).start();
    }
}
