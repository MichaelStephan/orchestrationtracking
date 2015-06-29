package io.yaas.workflow.runtime.action.instance;

import com.google.common.util.concurrent.SettableFuture;
import io.yaas.workflow.action.ActionResult;
import io.yaas.workflow.action.Arguments;
import io.yaas.workflow.runtime.ActionInstance;
import io.yaas.workflow.runtime.tracker.model.ActionBean;
import io.yaas.workflow.runtime.tracker.model.State;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by D032705 on 13.05.2015.
 */
public class SplitCompensationActionInstance extends SimpleActionInstance {

    private ActionInstance actionInstance;
    private AtomicInteger count;
    private List<ActionResult> results = new CopyOnWriteArrayList<>();
    private Object lastCreatedTimestampLock = new Object();

    public SplitCompensationActionInstance(ActionInstance actionInstance, int count) {
        super("compensation", actionInstance.getAction());
        this.actionInstance = actionInstance;
        this.count = new AtomicInteger(count);
    }

    @Override
    public void start(WorkflowInstance workflowInstance) {
        synchronized (lastCreatedTimestampLock) {
            if (this.lastCreatedTimestamp == null) {
                this.lastCreatedTimestamp = workflowInstance.getTrackingClient().createAction(new ActionBean(workflowInstance.getId(), getName(), getVersion(), getId())).timestamp;
            }
        }
    }

    public ActionInstance getActionInstance() {
        return actionInstance;
    }

    @Override
    public void succeed(WorkflowInstance workflowInstance, ActionResult result) {
        ActionBean actionBean = new ActionBean(workflowInstance.getId(), getName(), getVersion(), getId(), lastCreatedTimestamp);
        actionBean.astate = State.SUCCEEDED;
        workflowInstance.getTrackingClient().updateAction(actionBean);
    }

    @Override
    public void execute(WorkflowInstance workflowInstance, Arguments arguments, SettableFuture<ActionResult> result) {
        new Thread(() -> {
            results.add(new ActionResult(arguments));
            if (count.decrementAndGet() == 0) {
                // TODO make nicer !!!
                Map<String, Object> consolidatedArguments = new HashMap<>();
                results.stream().forEach((argument) -> {
                    consolidatedArguments.putAll(argument.getResult());
                });

                result.set(new ActionResult(new Arguments(consolidatedArguments)));
            }
        }).start();
    }
}
