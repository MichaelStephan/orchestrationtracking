package io.yaas.workflow;

import io.yaas.workflow.runtime.ActionInstance;
import io.yaas.workflow.runtime.action.instance.WorkflowInstance;
import io.yaas.workflow.runtime.traversal.TraversalStrategy;


public interface ActionErrorHandler {
    void execute(WorkflowInstance workflowInstance, ActionInstance actionInstance, Arguments arguments, Throwable cause);

    TraversalStrategy getTraversalStrategy();
}
