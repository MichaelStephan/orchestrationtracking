package io.yaas.workflow.runtime.execution;

import com.google.common.util.concurrent.SettableFuture;
import io.yaas.workflow.ActionErrorHandler;
import io.yaas.workflow.ActionResult;
import io.yaas.workflow.Arguments;
import io.yaas.workflow.errorhandler.FailFastErrorHandler;
import io.yaas.workflow.runtime.ActionInstance;
import io.yaas.workflow.runtime.action.instance.WorkflowInstance;
import io.yaas.workflow.runtime.traversal.BackwardTraversal;

/**
 * Created by D032705 on 30.04.2015.
 */
public class RecoveryExecutor extends AbstractExecutor {
    protected static ExecutionStrategy INSTANCE;

    public static ExecutionStrategy getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new RecoveryExecutor();
        }
        return INSTANCE;
    }
    public RecoveryExecutor() {
        super(BackwardTraversal.getInstance());
    }

    @Override
    public void start(WorkflowInstance workflow, ActionInstance action) {
    }

    @Override
    public void success(WorkflowInstance workflow, ActionInstance action) {

    }

    @Override
    public void error(WorkflowInstance workflow, ActionInstance action, Arguments arguments, Throwable cause) {
        new FailFastErrorHandler().execute(workflow, action, arguments, cause);
    }

    @Override
    public void execute(WorkflowInstance workflow, ActionInstance action, Arguments arguments, SettableFuture<ActionResult> result) {
        ActionErrorHandler errorHandler = action.getAction().getErrorHandler();
        if (errorHandler == null) {
            errorHandler = new FailFastErrorHandler();
        }
        errorHandler.execute(workflow, action, arguments, arguments.getError());
    }
}
