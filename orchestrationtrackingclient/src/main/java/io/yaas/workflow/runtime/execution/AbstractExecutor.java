package io.yaas.workflow.runtime.execution;

import io.yaas.workflow.action.ActionResult;
import io.yaas.workflow.action.Arguments;
import io.yaas.workflow.runtime.ActionInstance;
import io.yaas.workflow.runtime.WorkflowInstance;
import io.yaas.workflow.runtime.traversal.TraversalStrategy;

import java.util.Collection;

/**
 * Created by D032705 on 30.04.2015.
 */
public abstract class AbstractExecutor implements ExecutionStrategy {
    @Override
    public Collection<ActionInstance> getNext(ActionInstance action) {
        return getTraversalStrategy(action).getNext(action);
    }

    public abstract TraversalStrategy getTraversalStrategy(ActionInstance action);

    @Override
    public void start(WorkflowInstance workflow, ActionInstance action) {
        action.start(workflow);
    }

    @Override
    public void success(WorkflowInstance workflow, ActionResult result, ActionInstance action) {
        action.succeed(workflow, result);
    }

    @Override
    public void error(WorkflowInstance workflow, ActionInstance action, Arguments arguments, Throwable cause) {
        action.error(workflow, cause);
    }
}
