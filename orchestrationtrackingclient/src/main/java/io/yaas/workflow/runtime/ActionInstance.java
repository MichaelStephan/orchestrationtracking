package io.yaas.workflow.runtime;

import com.google.common.util.concurrent.SettableFuture;

import io.yaas.workflow.ActionResult;
import io.yaas.workflow.Arguments;
import io.yaas.workflow.runtime.action.instance.WorkflowInstance;
import io.yaas.workflow.runtime.tracker.client.WorkflowTrackingClient;

import java.util.Collection;

/**
 * Created by i303874 on 4/28/15.
 */

public interface ActionInstance {

    void addSuccessor(ActionInstance action);

    void addPredecessor(ActionInstance action);

    Collection<ActionInstance> getSuccessors();

    Collection<ActionInstance> getPredecessors();

    String getName();

    String getVersion();

    void start(WorkflowInstance workflowInstance, WorkflowTrackingClient client);

    void succeed(WorkflowInstance workflowInstance, WorkflowTrackingClient client);

    void error(WorkflowInstance workflowInstance, WorkflowTrackingClient client, Throwable cause);

    void execute(WorkflowInstance workflowInstance, WorkflowTrackingClient client, Arguments arguments, SettableFuture<ActionResult> result);
}
