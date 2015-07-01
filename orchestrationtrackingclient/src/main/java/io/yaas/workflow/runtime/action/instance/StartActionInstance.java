package io.yaas.workflow.runtime.action.instance;

import com.google.common.util.concurrent.SettableFuture;
import io.yaas.workflow.action.Action;
import io.yaas.workflow.action.ActionResult;
import io.yaas.workflow.action.Arguments;
import io.yaas.workflow.runtime.ActionInstance;

import java.util.Collections;

/**
 * Created by i303874 on 4/29/15.
 */
public class StartActionInstance extends SimpleActionInstance {
    private StartCompensationActionInstance compensationActionInstance = null;

    private Object compensationActionInstanceLock = new Object();

    public StartActionInstance(String id, Action action) {
        super(id, action);
    }

    public void start(WorkflowInstance workflowInstance) {
        workflowInstance.start();
        super.start(workflowInstance);
    }

    @Override
    public ActionInstance getCompensationActionInstance() {
        synchronized (compensationActionInstanceLock) {
            if (compensationActionInstance == null) {
                compensationActionInstance = new StartCompensationActionInstance(this);
            }
            return compensationActionInstance;
        }
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
