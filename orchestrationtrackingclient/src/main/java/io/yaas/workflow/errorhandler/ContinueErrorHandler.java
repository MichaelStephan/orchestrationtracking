package io.yaas.workflow.errorhandler;

import io.yaas.workflow.ActionErrorHandler;
import io.yaas.workflow.Arguments;
import io.yaas.workflow.runtime.ActionInstance;
import io.yaas.workflow.runtime.action.instance.WorkflowInstance;
import io.yaas.workflow.runtime.traversal.ForwardTraversal;
import io.yaas.workflow.runtime.traversal.TraversalStrategy;

import java.util.Collections;

/**
 * Created by i303874 on 4/29/15.
 */
public class ContinueErrorHandler implements ActionErrorHandler {
    @Override
    public void execute(WorkflowInstance workflowInstance, ActionInstance actionInstance, Arguments arguments, Throwable cause) {
        actionInstance.getPredecessors().stream().forEach(predecessor -> {
            ActionErrorHandler errorHandler = predecessor.getAction().getErrorHandler();
            if (errorHandler == null) {
                errorHandler = new ContinueErrorHandler();
            }
            errorHandler.execute(workflowInstance, predecessor, new Arguments(Collections.emptyMap()), new Exception());
        });
    }

    @Override
    public TraversalStrategy getTraversalStrategy() {
        return ForwardTraversal.getInstance();
    }
}
