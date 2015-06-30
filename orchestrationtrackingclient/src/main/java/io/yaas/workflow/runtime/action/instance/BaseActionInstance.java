package io.yaas.workflow.runtime.action.instance;

import io.yaas.workflow.runtime.ActionInstance;

import java.util.*;

/**
 * Created by D032705 on 29.06.2015.
 */
public abstract class BaseActionInstance implements ActionInstance {

    protected String id;
    protected Set<ActionInstance> predecessors = new HashSet<>();
    protected Set<ActionInstance> successors = new HashSet<>();

    public void addSuccessor(ActionInstance successor) {
        _addSuccessor(successor);
        successor._addPredecessor(this);
    }

    public void removeSuccessor(ActionInstance successor) {
        _removeSuccessor(successor);
        successor._removePredecessor(this);
    }

    public void addPredecessor(ActionInstance predecessor) {
        _addPredecessor(predecessor);
        predecessor._addSuccessor(this);
    }

    public void removePredecessor(ActionInstance predecessor) {
        _removePredecessor(predecessor);
        predecessor._removeSuccessor(this);
    }

    public void _addSuccessor(ActionInstance successor) {
        successors.add(successor);
    }

    public void _removeSuccessor(ActionInstance successor) {
        successors.remove(successor);
    }

    public void _addPredecessor(ActionInstance predecessor) {
        predecessors.add(predecessor);
    }

    public void _removePredecessor(ActionInstance predecessor) {
        predecessors.remove(predecessor);
    }

    @Override
    public void insertAfter(ActionInstance predecessor) {
        List<ActionInstance> successors = new ArrayList<ActionInstance>();
        successors.addAll(getSuccessors()); // clone, otherwise concurrent modification
        for (ActionInstance a : successors) {
            a.addPredecessor(predecessor);
            a.removePredecessor(this);
        }
        predecessor.addPredecessor(this);
    }

    @Override
    public void insertBefore(ActionInstance successor) {
        List<ActionInstance> predecessors = new ArrayList<ActionInstance>();
        predecessors.addAll(getPredecessors());  // clone, otherwise concurrent modification
        for (ActionInstance a : predecessors) {
            a.addSuccessor(successor);
            a.removeSuccessor(this);
        }
        successor.addSuccessor(this);
    }

    @Override
    public Collection<ActionInstance> getSuccessors() {
        return successors;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getVersion() {
        return null;
    }

    @Override
    public Set<ActionInstance> getPredecessors() {
        return this.predecessors;
    }

}
