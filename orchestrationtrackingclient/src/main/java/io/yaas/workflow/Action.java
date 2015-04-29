package io.yaas.workflow;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class Action {

    private String _name;
    private String _version;

    private Set<Action> _successors = new TreeSet<>((o1, o2) -> {
        if (o1.getNameVersion() == null) return -1;
        else return o1.getNameVersion().compareTo(o2.getNameVersion());
    });

    private Set<Action> _predecessors = new HashSet<>();

    private Workflow _w;

    protected Body _body;

    private ActionErrorHandler _onError;

    private String getNameVersion() {
        if (getName() == null && getVersion() == null)
            return null;
        else if (getName() == null)
            return getVersion();
        else
            return getName() + "." + getVersion();
    }

    public Action(String name, String version, Workflow w) {
        _name = name;
        _version = version;
        _w = w;
    }

    public Action(String name, String version) {
        _name = name;
        _version = version;
    }

    public Action(String name, String version, Body f) {
        _name = name;
        _version = version;
        _body = f;
    }

    public Action addAction(Action successor) {
        addSuccessor(successor);
        successor._w = this._w;
        return successor;
    }

    public Action setFunction(Body f) {
        if (_body != null)
            throw new IllegalStateException();
        _body = f;
        return this;
    }

    public Action setErrorHandler(ActionErrorHandler onError) {
        _onError = onError;

        return this;
    }

    public ActionErrorHandler getErrorHandler() {
        return _onError;
    }

    public Workflow getWorkflow() {
        return _w;
    }

    public void setWorkflow(Workflow w) {
        _w = w;
    }

    public Set<Action> getSuccessors() {
        return _successors;
    }

    public Body getFunction() {
        return _body;
    }

    public Set<Action> getPredecessors() {
        return _predecessors;
    }

    private void addSuccessor(Action successor) {
        _successors.add(successor);
        successor.addPredecessor(this);
    }

    private void removeSuccessor(Action successor) {
        _successors.remove(successor);
        successor.removePredecessor(this);
    }

    private void addPredecessor(Action predecessor) {
        _predecessors.add(predecessor);
    }

    private void removePredecessor(Action predecessor) {
        _predecessors.remove(predecessor);
    }

    void insertAfter(Action successor) {
        for (Action a : getSuccessors()) {
            successor.addSuccessor(a);
            removeSuccessor(a);
        }
        addSuccessor(successor);
    }

    void insertBefore(Action successor) {
        List<Action> predecessors = new ArrayList<Action>();
        for (Action a : getPredecessors()) { // otherwise concurrent modification
            predecessors.add(a);
        }
        for (Action a : predecessors) {
            a.addSuccessor(successor);
            a.removeSuccessor(this);
        }
        successor.addSuccessor(this);
    }

    public String getName() {
        return _name;
    }

    public String getVersion() {
        return _version;
    }

    public String toString() {
        return getName() + " \nSUCCESSORS: " + getSuccessors().stream().map((action) -> {
            return action.getName();
        }).collect(Collectors.toList());
    }

}

