package io.yaas.workflow.runtime.tracker.client;

/**
 * Created by i303874 on 7/1/15.
 */
public class WorkflowTrackingClientException extends RuntimeException {
    public WorkflowTrackingClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public WorkflowTrackingClientException(Throwable cause) {
        super(cause);
    }
}
