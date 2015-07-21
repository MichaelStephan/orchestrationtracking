package io.yaas.workflow.runtime.resulthandler;

import io.yaas.workflow.action.Arguments;
import io.yaas.workflow.runtime.WorkflowInstance;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by D032705 on 20.07.2015.
 */
public abstract class ChainedWorkflowEngineResultHandler implements WorkflowEngineResultHandler {
    private WorkflowEngineResultHandler wrappedHandler;

    public ChainedWorkflowEngineResultHandler() {
        this(new NullWorkflowEngineResultHandler());
    }
    public ChainedWorkflowEngineResultHandler(WorkflowEngineResultHandler wrappedHandler) {
        this.wrappedHandler = checkNotNull(wrappedHandler);
    }
    public void succeeded(WorkflowInstance workflow, Arguments results) {
        this.wrappedHandler.succeeded(workflow, results);
        _succeeded(workflow, results);
    }

    public void failed(WorkflowInstance workflow, Throwable cause) {
        this.wrappedHandler.failed(workflow, cause);
        _failed(workflow, cause);

    }

    public void compensated(WorkflowInstance workflow) {
        this.wrappedHandler.compensated(workflow);
        _compensated(workflow);
    }

    protected abstract void _succeeded(WorkflowInstance workflow, Arguments results);
    protected abstract void _failed(WorkflowInstance workflow, Throwable cause);
    protected abstract void _compensated(WorkflowInstance workflow);
}
