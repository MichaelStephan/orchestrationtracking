package io.yaas.workflow.errorhandler;

import io.yaas.workflow.ActionErrorHandler;
import io.yaas.workflow.Arguments;
import io.yaas.workflow.runtime.ActionInstance;
import io.yaas.workflow.runtime.action.instance.WorkflowInstance;

/**
 * Created by i303874 on 4/29/15.
 */
public class FailFastErrorHandler implements ActionErrorHandler {

    public FailFastErrorHandler() {
    }

    @Override
    public void execute(WorkflowInstance workflowInstance, ActionInstance actionInstance, Arguments arguments, Throwable cause) {
        workflowInstance.error();
    }
}
