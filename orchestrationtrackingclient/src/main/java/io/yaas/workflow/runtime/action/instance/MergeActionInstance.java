package io.yaas.workflow.runtime.action.instance;

import com.google.common.util.concurrent.SettableFuture;
import io.yaas.workflow.action.ActionResult;
import io.yaas.workflow.action.Arguments;
import io.yaas.workflow.action.MergeAction;
import io.yaas.workflow.runtime.WorkflowInstance;
import io.yaas.workflow.runtime.tracker.model.ActionBean;

import java.util.Collections;
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
    private Object lastCreatedTimestampLock = new Object();

    public MergeActionInstance(String id, MergeAction action, int count) {
        super(id, action);
        this._count = new AtomicInteger(count);
    }

    @Override
    public void start(WorkflowInstance workflowInstance) {
        synchronized (lastCreatedTimestampLock) {
            if (this.lastCreatedTimestamp == null) {
                this.lastCreatedTimestamp = workflowInstance.getTrackingClient().createAction(new ActionBean(workflowInstance.getId(), getName(), getVersion(), getId())).timestamp;
            }
        }
    }

    @Override
    public void execute(WorkflowInstance workflowInstance, Arguments arguments, SettableFuture<ActionResult> result) {
        _results.add(new ActionResult(arguments));
        if (_count.decrementAndGet() == 0) {
            Map<String, Object> consolidatedArguments = new HashMap<>();
            _results.stream().forEach((argument) -> {
                consolidatedArguments.putAll(argument.getResult());
            });

            result.set(new ActionResult(new Arguments(consolidatedArguments)));
        }
    }

    @Override
    public ActionResult restore(WorkflowInstance workflowInstance) {
        return new ActionResult(new Arguments(Collections.emptyMap()));
    }
}
