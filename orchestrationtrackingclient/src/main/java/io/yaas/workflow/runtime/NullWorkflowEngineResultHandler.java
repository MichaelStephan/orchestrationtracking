package io.yaas.workflow.runtime;

import io.yaas.workflow.action.Arguments;

/**
 * Created by D032705 on 20.07.2015.
 */
public class NullWorkflowEngineResultHandler implements WorkflowEngineResultHandler {
    @Override
    public void succeeded(WorkflowInstance workflow, Arguments results) {

    }

    @Override
    public void failed(WorkflowInstance workflow, Throwable cause) {

    }

    @Override
    public void compensated(WorkflowInstance workflow) {

    }
}
