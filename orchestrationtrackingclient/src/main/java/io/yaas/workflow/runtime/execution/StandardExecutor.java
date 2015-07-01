package io.yaas.workflow.runtime.execution;

import com.google.common.util.concurrent.SettableFuture;
import io.yaas.workflow.action.ActionResult;
import io.yaas.workflow.action.Arguments;
import io.yaas.workflow.runtime.ActionInstance;
import io.yaas.workflow.runtime.action.instance.WorkflowInstance;
import io.yaas.workflow.runtime.traversal.ForwardTraversal;
import io.yaas.workflow.runtime.traversal.TraversalStrategy;

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

    public ExecutionStrategy getFallbackExecutionStrategy() {
        return new CompensationExecutor();
    }

    @Override
    public void execute(WorkflowInstance workflow, ActionInstance action, Arguments arguments, SettableFuture<ActionResult> result) {
        try {
            action.execute(workflow, arguments, result);
        } catch (Exception e) {
            result.setException(e);
        }
    }

    @Override
    public TraversalStrategy getTraversalStrategy(ActionInstance action) {
        return ForwardTraversal.getInstance();
    }
}
