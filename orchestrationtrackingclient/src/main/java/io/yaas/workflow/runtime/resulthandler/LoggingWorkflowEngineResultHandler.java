package io.yaas.workflow.runtime.resulthandler;

import io.yaas.workflow.action.Arguments;
import io.yaas.workflow.runtime.WorkflowInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by i303874 on 7/1/15.
 */
public class LoggingWorkflowEngineResultHandler extends ChainedWorkflowEngineResultHandler {
    private static final Logger logger = LoggerFactory.getLogger(LoggingWorkflowEngineResultHandler.class);

    public LoggingWorkflowEngineResultHandler() {
        super();
    }
    public LoggingWorkflowEngineResultHandler(WorkflowEngineResultHandler wrappedHandler) {
        super(wrappedHandler);
    }
    @Override
    public void _succeeded(WorkflowInstance workflow, Arguments results) {
        logger.info("workflow " + workflow.getId() + " succeeded");
    }

    @Override
    public void _failed(WorkflowInstance workflow, Throwable cause) {
        try {
            logger.error("workflow " + workflow.getId() + " failed", cause);
        } catch (Exception e) {
            logger.error("unknown workflow failed", cause);
        }
    }

    @Override
    public void _compensated(WorkflowInstance workflow) {
        logger.warn("workflow " + workflow.getId() + " compensated");
    }
}
