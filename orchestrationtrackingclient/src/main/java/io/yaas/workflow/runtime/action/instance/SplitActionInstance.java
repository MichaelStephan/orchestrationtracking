package io.yaas.workflow.runtime.action.instance;

import com.google.common.util.concurrent.SettableFuture;
import io.yaas.workflow.action.ActionResult;
import io.yaas.workflow.action.Arguments;
import io.yaas.workflow.action.SplitAction;
import io.yaas.workflow.runtime.ActionInstance;

import java.util.Collections;

/**
 * Created by i303874 on 4/29/15.
 */
public class SplitActionInstance extends SimpleActionInstance {

    private SplitCompensationActionInstance compensationActionInstance = null;

    private Object compensationActionInstanceLock = new Object();


    private int count;

    public SplitActionInstance(String id, SplitAction action, int count) {
        super(id, action);
        this.count = count;
    }

    @Override
    public ActionInstance getCompensationActionInstance() {
        synchronized (compensationActionInstanceLock) {
            if (compensationActionInstance == null) {
                compensationActionInstance = new SplitCompensationActionInstance(this, count);
            }
            return compensationActionInstance;
        }
    }

    @Override
    public ActionInstance createCompensationActionInstance() {
        return new SplitCompensationActionInstance(this, count);
    }

    @Override
    public void execute(WorkflowInstance workflowInstance, Arguments arguments, SettableFuture<ActionResult> result) {
        result.set(new ActionResult(arguments));
    }

    @Override
    public ActionResult restore(WorkflowInstance workflowInstance) {
        return new ActionResult(new Arguments(Collections.emptyMap()));
    }
}
