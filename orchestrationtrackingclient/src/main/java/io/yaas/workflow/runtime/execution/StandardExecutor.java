package io.yaas.workflow.runtime.execution;

import com.google.common.util.concurrent.SettableFuture;
import io.yaas.workflow.ActionResult;
import io.yaas.workflow.Arguments;
import io.yaas.workflow.runtime.ActionInstance;
import io.yaas.workflow.runtime.action.instance.WorkflowInstance;
import io.yaas.workflow.runtime.traversal.ForwardTraversal;

/**
 * Created by D032705 on 30.04.2015.
 */
public class StandardExecutor extends AbstractExecutor {
    protected static ExecutionStrategy INSTANCE;

    public static ExecutionStrategy getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new StandardExecutor();
        }
        return INSTANCE;
    }
    public StandardExecutor() {
        super(ForwardTraversal.getInstance());
    }

    @Override
    public void start(WorkflowInstance workflow, ActionInstance action) {
        action.start(workflow, workflow.getTrackingClient());
    }

    @Override
    public void success(WorkflowInstance workflow, ActionResult result, ActionInstance action) {
        action.succeed(workflow, result, workflow.getTrackingClient());
    }

    @Override
    public void error(WorkflowInstance workflow, ActionInstance action, Arguments arguments, Throwable cause) {
        action.error(workflow, workflow.getTrackingClient(), cause);
    }

    @Override
    public void execute(WorkflowInstance workflow, ActionInstance action, Arguments arguments, SettableFuture<ActionResult> result) {
        action.execute(arguments, result);
    }
}
