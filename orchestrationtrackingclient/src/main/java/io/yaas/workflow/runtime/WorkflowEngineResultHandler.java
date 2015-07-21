package io.yaas.workflow.runtime;

import io.yaas.workflow.action.Arguments;

/**
 * Created by i303874 on 7/1/15.
 */
public interface WorkflowEngineResultHandler {
    void succeeded(WorkflowInstance workflow, Arguments results);

    void failed(WorkflowInstance workflow, Throwable cause);

    void compensated(WorkflowInstance workflow);
}
