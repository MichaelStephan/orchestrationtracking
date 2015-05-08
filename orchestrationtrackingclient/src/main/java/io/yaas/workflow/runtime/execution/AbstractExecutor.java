package io.yaas.workflow.runtime.execution;

import io.yaas.workflow.runtime.ActionInstance;
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
}
