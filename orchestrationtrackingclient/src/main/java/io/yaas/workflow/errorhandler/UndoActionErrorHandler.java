package io.yaas.workflow.errorhandler;

import io.yaas.workflow.ActionErrorHandler;
import io.yaas.workflow.Arguments;
import io.yaas.workflow.ErrorBody;
import io.yaas.workflow.runtime.ActionInstance;
import io.yaas.workflow.runtime.action.instance.WorkflowInstance;
import io.yaas.workflow.runtime.traversal.BackwardTraversal;
import io.yaas.workflow.runtime.traversal.TraversalStrategy;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by i303874 on 4/29/15.
 */
public class UndoActionErrorHandler implements ActionErrorHandler {
    private ErrorBody function;

    public UndoActionErrorHandler(ErrorBody function) {
        this.function = checkNotNull(function);
    }

    @Override
    public void execute(WorkflowInstance workflowInstance, ActionInstance actionInstance, Arguments arguments, Throwable cause) {
        function.apply(cause, arguments);
    }

    @Override
    public TraversalStrategy getTraversalStrategy() {
        return BackwardTraversal.getInstance();
    }
}
