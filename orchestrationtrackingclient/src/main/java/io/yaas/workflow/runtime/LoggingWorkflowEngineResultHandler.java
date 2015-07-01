package io.yaas.workflow.runtime;

import io.yaas.workflow.action.Arguments;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by i303874 on 7/1/15.
 */
public class LoggingWorkflowEngineResultHandler implements WorkflowEngineResultHandler {
    private static final Logger logger = LoggerFactory.getLogger(LoggingWorkflowEngineResultHandler.class);

    @Override
    public void succeeded(String wid, Arguments results) {
        logger.info("workflow " + wid + " succeeded");
    }

    @Override
    public void failed(String wid, Throwable cause) {
        logger.error("workflow " + wid + " failed", cause);
    }

    @Override
    public void compensated(String wid) {
        logger.warn("workflow " + wid + " compensated");
    }
}
