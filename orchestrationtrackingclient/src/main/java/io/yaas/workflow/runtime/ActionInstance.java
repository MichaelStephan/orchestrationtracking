package io.yaas.workflow.runtime;

import com.google.common.util.concurrent.SettableFuture;
import io.yaas.workflow.action.Action;
import io.yaas.workflow.action.ActionResult;
import io.yaas.workflow.action.Arguments;
import io.yaas.workflow.runtime.action.instance.WorkflowInstance;

import java.util.Collection;
import java.util.Iterator;

/**
 * Created by i303874 on 4/28/15.
 */

public interface ActionInstance {
    Action getAction();

    String getId();

    void addSuccessor(ActionInstance successor);

    void removeSuccessor(ActionInstance successor);

    void addPredecessor(ActionInstance predecessor);

    void removePredecessor(ActionInstance predecessor);

    void _addSuccessor(ActionInstance successor);

    void _removeSuccessor(ActionInstance successor);

    void _addPredecessor(ActionInstance predecessor);

    void _removePredecessor(ActionInstance predecessor);

    void insertAfter(ActionInstance predecessor);

    void insertBefore(ActionInstance successor);

    Collection<ActionInstance> getSuccessors();

    Collection<ActionInstance> getPredecessors();

    Iterator<ActionInstance> iterator();

    String getName();

    String getVersion();

    ActionInstance getCompensationActionInstance();

    // TODO clean WorkflowTrackingClient client
    void start(WorkflowInstance workflowInstance);

    void succeed(WorkflowInstance workflowInstance, ActionResult result);

    void error(WorkflowInstance workflowInstance, Throwable cause);

    void execute(WorkflowInstance workflowInstance, Arguments arguments, SettableFuture<ActionResult> result);

    ActionResult restore(WorkflowInstance workflowInstance);
}
