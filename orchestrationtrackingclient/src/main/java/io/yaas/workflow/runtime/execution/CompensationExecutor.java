package io.yaas.workflow.runtime.execution;

import com.google.common.util.concurrent.SettableFuture;
import io.yaas.workflow.action.ActionFunction;
import io.yaas.workflow.action.ActionResult;
import io.yaas.workflow.action.Arguments;
import io.yaas.workflow.runtime.ActionInstance;
import io.yaas.workflow.runtime.action.instance.NOPComensationFunction;
import io.yaas.workflow.runtime.action.instance.WorkflowInstance;
import io.yaas.workflow.runtime.traversal.BackwardTraversal;
import io.yaas.workflow.runtime.traversal.TraversalStrategy;

/**
 * Created by D032705 on 30.04.2015.
 */
public class CompensationExecutor extends AbstractExecutor implements ExecutionStrategy {

    @Override
    public void start(WorkflowInstance workflow, ActionInstance action) {
    }

    @Override
    public void success(WorkflowInstance workflow, ActionResult result, ActionInstance action) {
    }

    @Override
    public void error(WorkflowInstance workflow, ActionInstance action, Arguments arguments, Throwable cause) {
//        new FailFastErrorHandler().execute(workflow, action, arguments, cause);
    }

    @Override
    public void execute(WorkflowInstance workflow, ActionInstance action, Arguments arguments, SettableFuture<ActionResult> result) {
        ActionFunction compensation = action.getAction().getCompensationFunction();
        if (compensation == null) {
            compensation = new NOPComensationFunction();
        }

        // action.restore(workflow).getResult()
        result.set(compensation.apply(Arguments.EMPTY_ARGUMENTS));
    }

    public ExecutionStrategy getActionErrorStrategy() {
        return new FailureExecutor();
    }

    @Override
    public TraversalStrategy getTraversalStrategy(ActionInstance action) {
        return BackwardTraversal.getInstance();
    }
}
