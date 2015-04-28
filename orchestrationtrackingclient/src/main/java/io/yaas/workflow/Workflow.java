package io.yaas.workflow;

import io.yaas.workflow.runtime.WorkflowEngine;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Workflow {

    private String _name;
    private int _version;

    private Action _startAction;

    private ErrorHandler _onFailureHandler;
    private ErrorHandler _onUnknownHandler;

    private Map<String, Action> _actionsRegistry = new HashMap<>();

    public Workflow(String name, int version) {
        _name = name;
        _version = version;
    }

    public String getName() {
        return _name;
    }

    public int getVersion() {
        return _version;
    }

    public String getDigest() {
        return ""; // TODO
    }

    public Workflow setErrorHandler(ErrorHandler onFailure, ErrorHandler onUnknown) {
        _onFailureHandler = onFailure;
        _onUnknownHandler = onUnknown;

        return this;
    }

    public ErrorHandler getOnFailure() {
        return _onFailureHandler;
    }

    public ErrorHandler getOnUnknown() {
        return _onUnknownHandler;
    }

    public Action getStartAction() {
        if (_startAction == null) {
            _startAction = new Action("Start action", "0.0", this);
            _startAction.setFunction((actionInstance, arguments) -> {
                return new ActionResult(actionInstance, new Arguments(arguments));
            });
        }
        return _startAction;
    }

    private String generateId(Action a, int i) {
        return (a.getName() + "." + a.getVersion() + "." + i).replaceAll("\\s", "_");
    }

    public Action getAction(String id) {
        return _actionsRegistry.get(id);
    }

    public void execute(WorkflowEngine engine, Arguments arguments) {
        prepareExecute();
        engine.runWorkflow(this, arguments);
    }

    private void prepareExecute() {
        insertMergeActions();
        List<Action> linearized = getLinearizedActions();
        for (int i = 0; i < linearized.size(); i++) {
            registerAction(linearized.get(i), i);
        }
    }

    private void insertMergeActions() {
        mergeAction(getStartAction());
    }

    private void mergeAction(Action a) {
        for (Action successor : a.getSuccessors()) {
            if (a.getPredecessors().size() > 1 && !(a instanceof MergeAction)) {
                a.insertBefore(new MergeAction(this));
            }
            mergeAction(successor);
        }
    }

    private List<Action> getLinearizedActions() {
        List<Action> sorted = new LinkedList<Action>();
        Set<Action> visited = new HashSet<>();
        visitAction(visited, sorted, _startAction);
        return sorted;
    }

    private void visitAction(Set<Action> visited, List<Action> sorted, Action a) {
        if (!visited.contains(a)) {
            sorted.add(a);
            visited.add(a);

            for (Action successor : a.getSuccessors()) {
                visitAction(visited, sorted, successor);
            }
        }
    }

    private String registerAction(Action a, int i) {
        String id = generateId(a, i);
        _actionsRegistry.put(id, a);
        a.setId(id);
        return id;
    }

}
