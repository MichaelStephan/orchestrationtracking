package io.yaas.workflow.action;

import io.yaas.workflow.Workflow;
import io.yaas.workflow.runtime.action.instance.NOPCompensationFunction;
import io.yaas.workflow.runtime.traversal.ForwardIterator;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by D032705 on 15.05.2015.
 */
abstract class BaseAction implements Action {

    protected String name;
    protected String version;
    protected Workflow workflow;
    protected ActionFunction actionFunction;
    //    protected ActionErrorHandler _onError;
    protected ActionFunction compensationFunction = new NOPCompensationFunction();

    protected Set<Action> successors = new TreeSet<>((o1, o2) -> {
        if (o1.getId() == null) return -1;
        else return o1.getId().compareTo(o2.getId());
    });

    protected Set<Action> predecessors = new HashSet<Action>();

    public BaseAction(String name, String version, Workflow w) {
        this.name = name;
        this.version = version;
        workflow = w;
    }

    public BaseAction(String name, String version) {
        this.name = name;
        this.version = version;
    }

    public BaseAction(String name, String version, ActionFunction f) {
        this.name = name;
        this.version = version;
        actionFunction = f;
    }

    protected BaseAction(ActionFunction f) {
        actionFunction = f;
    }

    public String getId() {
        return getNameVersion();
    }

    public Collection<Action> getPredecessors() {
        return predecessors;
    }

    public Collection<Action> getSuccessors() {
        return successors;
    }

    public void addSuccessor(Action successor) {
        _addSuccessor(successor);
        successor._addPredecessor(this);
    }

    public void removeSuccessor(Action successor) {
        _removeSuccessor(successor);
        successor._removePredecessor(this);
    }

    public void addPredecessor(Action predecessor) {
        _addPredecessor(predecessor);
        predecessor._addSuccessor(this);
    }

    public void removePredecessor(Action predecessor) {
        _removePredecessor(predecessor);
        predecessor._removeSuccessor(this);
    }

    public void _addSuccessor(Action successor) {
        successors.add(successor);
    }

    public void _removeSuccessor(Action successor) {
        successors.remove(successor);
    }

    public void _addPredecessor(Action predecessor) {
        predecessors.add(predecessor);
    }

    public void _removePredecessor(Action predecessor) {
        predecessors.remove(predecessor);
    }

    @Override
    public void insertAfter(Action predecessor) {
        List<Action> successors = new ArrayList<Action>();
        successors.addAll(getSuccessors()); // clone, otherwise concurrent modification
        for (Action a : successors) {
            a.addPredecessor(predecessor);
            a.removePredecessor(this);
        }
        predecessor.addPredecessor(this);
    }

    @Override
    public void insertBefore(Action successor) {
        List<Action> predecessors = new ArrayList<Action>();
        predecessors.addAll(getPredecessors());  // clone, otherwise concurrent modification
        for (Action a : predecessors) {
            a.addSuccessor(successor);
            a.removeSuccessor(this);
        }
        successor.addSuccessor(this);
    }

    private String getNameVersion() {
        if (getName() == null && getVersion() == null)
            return null;
        else if (getName() == null)
            return getVersion();
        else
            return getName() + "." + getVersion();
    }

    @Override
    public Action setActionFunction(ActionFunction f) {
        if (actionFunction != null)
            throw new IllegalStateException();
        actionFunction = f;
        return this;
    }

    @Override
    public Action setCompensationFunction(ActionFunction f) {
        this.compensationFunction = f;
        return this;
    }

    @Override
    public ActionFunction getCompensationFunction() {
        return compensationFunction;
    }

    @Override
    public Action addAction(Action successor) {
        addSuccessor(successor);
        successor.setWorkflow(this.workflow);
        return successor;
    }

//    public Action setErrorHandler(ActionErrorHandler onError) {
//        _onError = onError;
//
//        return this;
//    }
//
//    public ActionErrorHandler getErrorHandler() {
//        return _onError == null ?
//                new UndoActionErrorHandler((cause, arguments) -> null)
//                : _onError;
//    }

    @Override
    public Workflow getWorkflow() {
        return workflow;
    }

    public void setWorkflow(Workflow w) {
        workflow = w;
    }

    @Override
    public ActionFunction getActionFunction() {
        return actionFunction;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getVersion() {
        return version;
    }

    public String toString() {
        return getName() + " \nSUCCESSORS: " + getSuccessors().stream().map((action) -> {
            return action.getName();
        }).collect(Collectors.toList());
    }
}
