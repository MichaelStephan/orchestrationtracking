package io.yaas.workflow.runtime.execution;

import com.google.common.util.concurrent.SettableFuture;
import io.yaas.workflow.action.ActionResult;
import io.yaas.workflow.action.Arguments;
import io.yaas.workflow.runtime.ActionInstance;
import io.yaas.workflow.runtime.action.instance.WorkflowInstance;
import io.yaas.workflow.runtime.traversal.BackwardTraversal;
import io.yaas.workflow.runtime.traversal.TraversalStrategy;

/**
 * Created by D032705 on 30.04.2015.
 */
public class CompensationExecutor extends AbstractExecutor implements ExecutionStrategy {

    @Override
    public void execute(WorkflowInstance workflow, ActionInstance action, Arguments arguments, SettableFuture<ActionResult> result) {
        action.getCompensationActionInstance().execute(workflow, action.restore(workflow).getResult(), result);
    }

    @Override
    public void start(WorkflowInstance workflow, ActionInstance action) {
        action.getCompensationActionInstance().start(workflow);
    }

    @Override
    public void success(WorkflowInstance workflow, ActionResult result, ActionInstance action) {
        action.getCompensationActionInstance().succeed(workflow, result);
    }

    @Override
    public void error(WorkflowInstance workflow, ActionInstance action, Arguments arguments, Throwable cause) {
        action.getCompensationActionInstance().error(workflow, cause);
    }

    public ExecutionStrategy getFallbackExecutionStrategy() {
        return new FailureExecutor();
    }

    @Override
    public TraversalStrategy getTraversalStrategy(ActionInstance action) {
        return BackwardTraversal.getInstance();
    }
}
