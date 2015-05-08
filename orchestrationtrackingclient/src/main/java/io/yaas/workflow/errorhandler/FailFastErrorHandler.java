package io.yaas.workflow.errorhandler;

import io.yaas.workflow.ActionErrorHandler;
import io.yaas.workflow.Arguments;
import io.yaas.workflow.runtime.ActionInstance;
import io.yaas.workflow.runtime.action.instance.WorkflowInstance;
import io.yaas.workflow.runtime.traversal.StopTraversal;
import io.yaas.workflow.runtime.traversal.TraversalStrategy;

/**
 * Created by i303874 on 4/29/15.
 */
public class FailFastErrorHandler implements ActionErrorHandler {

    @Override
    public void execute(WorkflowInstance workflowInstance, ActionInstance actionInstance, Arguments arguments, Throwable cause) {
        workflowInstance.error();
    }

    @Override
    public TraversalStrategy getTraversalStrategy() {
        return StopTraversal.getInstance();
    }
}
