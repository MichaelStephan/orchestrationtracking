package io.yaas.workflow;

import io.yaas.workflow.runtime.ActionInstance;
import io.yaas.workflow.runtime.action.instance.EndActionInstance;
import io.yaas.workflow.runtime.action.instance.MergeActionInstance;
import io.yaas.workflow.runtime.action.instance.SimpleActionInstance;
import io.yaas.workflow.runtime.WorkflowEngine;
import io.yaas.workflow.runtime.action.instance.StartActionInstance;

import java.util.*;

public class Workflow {

    private String _name;
    private int _version;

    private Action _startAction;

    private ErrorHandler _onFailureHandler;
    private ErrorHandler _onUnknownHandler;

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
            _startAction = new StartAction();
        }
        return _startAction;
    }

    private void print(ActionInstance action) {
        System.out.println("------");
        System.out.println(action.hashCode());
        System.out.println(action);
        for (ActionInstance s : action.getSuccessors()) {
            print(s);
        }
    }

    private ActionInstance transform(Action action, int nextId) {
        String id = Integer.toString(nextId);

        if (action instanceof MergeAction) {
            return new MergeActionInstance(id, MergeAction.class.cast(action), action.getPredecessors().size());
        } else if (action instanceof StartAction) {
            return new StartActionInstance(id, action);
        } else if (action instanceof EndAction) {
            return new EndActionInstance(id, action);
        } else {
            return new SimpleActionInstance(id, action);
        }
    }

    public void execute(WorkflowEngine engine, Arguments arguments) {
        ActionInstance start = prepareExecute();

        engine.runWorkflow(this, start, arguments);
    }

    private ActionInstance prepareExecute() {
        insertMergeActions(getStartAction());
        insertEndAction(getStartAction());
        return getActionInstances();
    }

    private void insertEndAction(Action a) {
        for (Action successor : a.getSuccessors()) {
            insertEndAction(successor);
        }

        if (a.getSuccessors().isEmpty() && !(a instanceof EndAction)) {
            a.addAction(new EndAction());
        }
    }

    private void insertMergeActions(Action a) {
        for (Action successor : a.getSuccessors()) {
            if (a.getPredecessors().size() > 1 && !(a instanceof MergeAction)) {
                a.insertBefore(new MergeAction(this));
            }
            insertMergeActions(successor);
        }
    }

    private ActionInstance getActionInstances() {
        Map<Action, ActionInstance> action2InstanceMapping = new HashMap<>();
        return visitAction(getStartAction(), action2InstanceMapping, 0);
    }

    private ActionInstance visitAction(Action action, Map<Action, ActionInstance> action2InstanceMapping, int nextId) {
        ActionInstance instance = action2InstanceMapping.get(action);
        if (instance == null) {
            instance = transform(action, nextId);
            action2InstanceMapping.put(action, instance);

            for (Action successor : action.getSuccessors()) {
                ActionInstance successorActionInstance = visitAction(successor, action2InstanceMapping, ++nextId);
                instance.addSuccessor(successorActionInstance);
                successorActionInstance.addPredecessor(instance);
            }
        }
        return instance;
    }
}
