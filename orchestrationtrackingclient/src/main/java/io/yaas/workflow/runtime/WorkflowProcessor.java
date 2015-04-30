package io.yaas.workflow.runtime;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.SettableFuture;
import io.yaas.workflow.ActionResult;
import io.yaas.workflow.Arguments;
import io.yaas.workflow.runtime.action.instance.WorkflowInstance;
import io.yaas.workflow.runtime.execution.ExecutionStrategy;
import io.yaas.workflow.runtime.execution.RecoveryExecutor;

/**
 * Created by D032705 on 30.04.2015.
 */
public class WorkflowProcessor {
    // private ExecutionStrategy executor = StandardExecutor.getInstance();
    private WorkflowInstance workflow;
    public WorkflowProcessor(WorkflowInstance workflow) {
        this.workflow = workflow;
    }

    public void process(ExecutionStrategy executor, ActionInstance action, Arguments arguments) {
        SettableFuture<ActionResult> future = SettableFuture.create();
        Futures.addCallback(future, new FutureCallback<ActionResult>() {
            @Override
            public void onSuccess(ActionResult result) {
                executor.success(workflow, action);

                executor.getNext(action).forEach((next) -> {
                    process(executor, next, result.getResult());
                });
            }

            @Override
            public void onFailure(Throwable cause) {
                executor.error(workflow, action, arguments, cause);
                // throw new Exception(cause);
                arguments.addError(cause);
                process(RecoveryExecutor.getInstance(), action, arguments);
            }
        });
        executor.start(workflow, action);
        executor.execute(workflow, action, arguments, future);
    }
}
