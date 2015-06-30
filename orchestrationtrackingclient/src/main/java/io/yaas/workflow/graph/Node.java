package io.yaas.workflow.graph;

import io.yaas.workflow.action.ActionFunction;

import java.util.Collection;
import java.util.Iterator;

/**
 * Created by D032705 on 30.06.2015.
 */
public interface Node <T extends Node> {
    String getId();

    Collection<T> getSuccessors();
    Collection<T> getPredecessors();

    void insertAfter(T predecessor);

    void insertBefore(T successor);

    void addSuccessor(T successor);
    void removeSuccessor(T successor);
    void addPredecessor(T predecessor);
    void removePredecessor(T predecessor);

    void _addSuccessor(T successor);
    void _removeSuccessor(T successor);
    void _addPredecessor(T predecessor);
    void _removePredecessor(T predecessor);
}
