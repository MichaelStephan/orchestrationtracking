package io.yaas.workflow.runtime.execution;

import com.google.common.util.concurrent.SettableFuture;
import io.yaas.workflow.action.ActionResult;
import io.yaas.workflow.action.Arguments;
import io.yaas.workflow.runtime.ActionInstance;
import io.yaas.workflow.runtime.action.instance.WorkflowInstance;
import io.yaas.workflow.runtime.traversal.BackwardTraversal;
import io.yaas.workflow.runtime.traversal.TraversalStrategy;

import java.util.Collections;

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
        // new FailFastErrorHandler().execute(workflow, action, arguments, cause);
    }

    @Override
    public void execute(WorkflowInstance workflow, ActionInstance action, Arguments arguments, SettableFuture<ActionResult> result) {
//        // TODO error in error still issue
//        ActionErrorHandler errorHandler = action.getAction().getErrorHandler();
//        if (errorHandler == null) {
//            errorHandler = new FailFastErrorHandler();
//        }
//        try {
//            errorHandler.execute(workflow, action, arguments, arguments.getError());
//            action.errorHandlerSuccess(workflow, workflow.getTrackingClient());
//            result.set(new ActionResult(new Arguments(Collections.emptyMap())));
//        } catch (Exception e) {
//            action.errorHandlerError(workflow, workflow.getTrackingClient());
//            result.setException(e);
//        }
    }

    @Override
    public TraversalStrategy getTraversalStrategy(ActionInstance action) {
//        ActionErrorHandler errorHandler = action.getAction().getErrorHandler();
//        return errorHandler == null ? BackwardTraversal.getInstance() : errorHandler.getTraversalStrategy();
        return null;
    }
}
