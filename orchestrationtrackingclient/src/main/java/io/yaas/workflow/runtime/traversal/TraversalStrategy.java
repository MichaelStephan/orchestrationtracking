package io.yaas.workflow.runtime.traversal;

import io.yaas.workflow.runtime.ActionInstance;

import java.util.Collection;

/**
 * Created by D032705 on 30.04.2015.
 */
public interface TraversalStrategy {
    Collection<ActionInstance> getNext(ActionInstance current);
}
