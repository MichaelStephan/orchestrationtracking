package io.yaas.workflow.runtime;

import io.yaas.workflow.ActionErrorHandler;
import io.yaas.workflow.Arguments;
import io.yaas.workflow.runtime.action.instance.WorkflowInstance;

/**
 * Created by i303874 on 4/29/15.
 */
public class FailFastActionErrorHandler implements ActionErrorHandler {

    public FailFastActionErrorHandler() {
    }

    @Override
    public void execute(WorkflowInstance workflowInstance, ActionInstance actionInstance, Arguments arguments, Throwable cause) {
        workflowInstance.error();
    }
}
