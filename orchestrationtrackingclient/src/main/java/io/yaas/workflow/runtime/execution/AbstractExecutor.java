package io.yaas.workflow.runtime.execution;

import io.yaas.workflow.runtime.ActionInstance;
import io.yaas.workflow.runtime.traversal.TraversalStrategy;

import java.util.Collection;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by D032705 on 30.04.2015.
 */
public abstract class AbstractExecutor implements ExecutionStrategy {
    protected TraversalStrategy traversal;

    protected AbstractExecutor(TraversalStrategy traversal) {
        this.traversal = checkNotNull(traversal);
    }
    @Override
    public Collection<ActionInstance> getNext(ActionInstance actionInstance) {
        return traversal.getNext(actionInstance);
    }
}
