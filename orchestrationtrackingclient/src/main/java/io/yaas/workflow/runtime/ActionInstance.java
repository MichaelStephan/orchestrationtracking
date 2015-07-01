package io.yaas.workflow.runtime;

import com.google.common.util.concurrent.SettableFuture;
import io.yaas.workflow.action.Action;
import io.yaas.workflow.action.ActionResult;
import io.yaas.workflow.action.Arguments;
import io.yaas.workflow.graph.Node;
import io.yaas.workflow.runtime.action.instance.WorkflowInstance;

import java.util.Collection;
import java.util.Iterator;

/**
 * Created by i303874 on 4/28/15.
 */

public interface ActionInstance extends Node<ActionInstance> {
    Action getAction();

    String getName();

    String getVersion();

    ActionInstance getCompensationActionInstance();

    void start(WorkflowInstance workflowInstance);

    void succeed(WorkflowInstance workflowInstance, ActionResult result);

    void error(WorkflowInstance workflowInstance, Throwable cause);

    void execute(WorkflowInstance workflowInstance, Arguments arguments, SettableFuture<ActionResult> result);

    ActionResult restore(WorkflowInstance workflowInstance);
}
