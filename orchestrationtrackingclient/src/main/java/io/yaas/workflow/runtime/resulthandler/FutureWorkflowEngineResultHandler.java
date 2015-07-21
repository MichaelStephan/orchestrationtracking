package io.yaas.workflow.runtime.resulthandler;

import com.google.common.util.concurrent.SettableFuture;
import io.yaas.workflow.action.Arguments;
import io.yaas.workflow.runtime.CompensatedException;
import io.yaas.workflow.runtime.WorkflowInstance;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by i303874 on 7/21/15.
 */
public class FutureWorkflowEngineResultHandler implements WorkflowEngineResultHandler {

    public SettableFuture<Arguments> getFuture() {
        return future;
    }

    private SettableFuture<Arguments> future;

    public FutureWorkflowEngineResultHandler() {
        future = SettableFuture.create();
    }

    public FutureWorkflowEngineResultHandler(SettableFuture<Arguments> future) {
        this.future = checkNotNull(future);
    }

    @Override
    public void succeeded(WorkflowInstance workflow, Arguments results) {
        future.set(results);
    }

    @Override
    public void failed(WorkflowInstance workflow, Throwable cause) {
        future.setException(cause);
    }

    @Override
    public void compensated(WorkflowInstance workflow) {
        future.setException(new CompensatedException());
    }
}
