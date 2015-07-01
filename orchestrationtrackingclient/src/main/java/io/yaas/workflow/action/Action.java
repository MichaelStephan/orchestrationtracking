package io.yaas.workflow.action;

import io.yaas.workflow.Workflow;
import io.yaas.workflow.graph.Node;

import java.util.Collection;

/**
 * Created by D032705 on 15.05.2015.
 */
public interface Action extends Node<Action> {

    Workflow getWorkflow();
    void setWorkflow(Workflow workflow);

    String getName();
    String getVersion();
    String getId();

    ActionFunction getActionFunction();
    ActionFunction getCompensationFunction();

    Action setActionFunction(ActionFunction f);
    Action setCompensationFunction(ActionFunction f);

    Action addAction(Action successor);

}
