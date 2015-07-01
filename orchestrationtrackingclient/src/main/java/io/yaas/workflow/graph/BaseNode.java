package io.yaas.workflow.graph;

import io.yaas.workflow.action.Action;
import io.yaas.workflow.graph.Node;

import java.util.*;

/**
 * Created by D032705 on 30.06.2015.
 */
public class BaseNode <T extends Node> implements Node <T> {

    private String id;
    protected Set<T> successors = new TreeSet<>((o1, o2) -> {
        if (o1.getId() == null) return -1;
        else return o1.getId().compareTo(o2.getId());
    });

    protected Set<T> predecessors = new HashSet<T>();

    public BaseNode(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public Collection<T> getPredecessors() {
        return Collections.unmodifiableCollection(predecessors);
    }

    public Collection<T> getSuccessors() {
        return Collections.unmodifiableCollection(successors);
    }

    public void addSuccessor(T successor) {
        _addSuccessor(successor);
        successor._addPredecessor(this);
    }

    public void removeSuccessor(T successor) {
        _removeSuccessor(successor);
        successor._removePredecessor(this);
    }

    public void addPredecessor(T predecessor) {
        _addPredecessor(predecessor);
        predecessor._addSuccessor(this);
    }

    public void removePredecessor(T predecessor) {
        _removePredecessor(predecessor);
        predecessor._removeSuccessor(this);
    }

    public void _addSuccessor(T successor) {
        successors.add(successor);
    }

    public void _removeSuccessor(T successor) {
        successors.remove(successor);
    }

    public void _addPredecessor(T predecessor) {
        predecessors.add(predecessor);
    }

    public void _removePredecessor(T predecessor) {
        predecessors.remove(predecessor);
    }

    @Override
    public void insertAfter(T predecessor) {
        List<T> successors = new ArrayList<T>();
        successors.addAll(getSuccessors()); // clone, otherwise concurrent modification
        for (T a : successors) {
            a.addPredecessor(predecessor);
            a.removePredecessor(this);
        }
        predecessor.addPredecessor(this);
    }

    @Override
    public void insertBefore(T successor) {
        List<T> predecessors = new ArrayList<T>();
        predecessors.addAll(getPredecessors());  // clone, otherwise concurrent modification
        for (T a : predecessors) {
            a.addSuccessor(successor);
            a.removeSuccessor(this);
        }
        successor.addSuccessor(this);
    }
}
