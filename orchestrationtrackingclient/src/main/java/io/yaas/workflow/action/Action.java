package io.yaas.workflow.action;

import io.yaas.workflow.Workflow;

import java.util.Collection;
import java.util.Iterator;

/**
 * Created by D032705 on 15.05.2015.
 */
public interface Action {

    Workflow getWorkflow();

    String getName();
    String getVersion();
    String getId();

    ActionFunction getActionFunction();
    ActionFunction getCompensationFunction();

    Collection<Action> getSuccessors();
    Collection<Action> getPredecessors();
    Iterator<Action> iterator();

    // Graph build
    // TODO refactor
    void insertAfter(Action predecessor);

    void insertBefore(Action successor);

    Action setActionFunction(ActionFunction f);
    Action setCompensationFunction(ActionFunction f);

    Action addAction(Action successor);

    void addSuccessor(Action successor);
    void removeSuccessor(Action successor);
    void addPredecessor(Action predecessor);
    void removePredecessor(Action predecessor);
    void _addSuccessor(Action successor);

    void _removeSuccessor(Action successor);

    void _addPredecessor(Action predecessor);

    void _removePredecessor(Action predecessor);

    // void setWorkflow(Workflow w);
}
