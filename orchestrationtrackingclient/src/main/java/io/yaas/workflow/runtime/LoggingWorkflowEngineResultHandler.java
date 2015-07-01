package io.yaas.workflow.runtime;

import io.yaas.workflow.action.Arguments;
import io.yaas.workflow.runtime.action.instance.WorkflowInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by i303874 on 7/1/15.
 */
public class LoggingWorkflowEngineResultHandler implements WorkflowEngineResultHandler {
    private static final Logger logger = LoggerFactory.getLogger(LoggingWorkflowEngineResultHandler.class);

    @Override
    public void succeeded(WorkflowInstance workflow, Arguments results) {
        logger.info("workflow " + workflow.getId() + " succeeded");
    }

    @Override
    public void failed(WorkflowInstance workflow, Throwable cause) {
        try {
            logger.error("workflow " + workflow.getId() + " failed", cause);
        } catch (Exception e) {
            logger.error("unknown workflow failed", cause);
        }
    }

    @Override
    public void compensated(WorkflowInstance workflow) {
        logger.warn("workflow " + workflow.getId() + " compensated");
    }
}
