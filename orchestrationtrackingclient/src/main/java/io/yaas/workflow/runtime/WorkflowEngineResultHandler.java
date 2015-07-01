package io.yaas.workflow.runtime;

import io.yaas.workflow.action.Arguments;

/**
 * Created by i303874 on 7/1/15.
 */
public interface WorkflowEngineResultHandler {
    void succeeded(String wid, Arguments results);

    void failed(String wid, Throwable cause);

    void compensated(String wid);
}
