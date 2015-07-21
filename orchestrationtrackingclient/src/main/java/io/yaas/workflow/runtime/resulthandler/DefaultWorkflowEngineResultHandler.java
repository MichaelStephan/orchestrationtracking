package io.yaas.workflow.runtime.resulthandler;

import io.yaas.workflow.action.Arguments;
import io.yaas.workflow.runtime.WorkflowInstance;

/**
 * Created by D032705 on 20.07.2015.
 */
public class DefaultWorkflowEngineResultHandler implements WorkflowEngineResultHandler {

    @Override
    public void succeeded(WorkflowInstance workflow, Arguments results) {
    }

    @Override
    public void failed(WorkflowInstance workflow, Throwable cause) {
        Arguments args = Arguments.EMPTY_ARGUMENTS;
        args.addError(cause);
        workflow.getWorkflow().getOnFailure().apply(args);
    }

    @Override
    public void compensated(WorkflowInstance workflow) {

    }
}
