package io.yaas.workflow;

import io.yaas.workflow.action.*;
import io.yaas.workflow.runtime.ActionInstance;
import io.yaas.workflow.runtime.WorkflowEngine;
import io.yaas.workflow.runtime.WorkflowEngineResultHandler;
import io.yaas.workflow.runtime.action.instance.*;

import java.util.HashMap;
import java.util.Map;

public class Workflow {

    private String _name;
    private int _version;

    private SimpleAction _startAction;

    private ActionFunction _onFailureHandler;

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

    public Workflow setErrorHandler(ActionFunction onFailure) {
        _onFailureHandler = onFailure;

        return this;
    }

    public ActionFunction getOnFailure() {
        return _onFailureHandler;
    }

    public SimpleAction getStartAction() {
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

        // Build ActionInstance graph
        if (action instanceof MergeAction) {
            return new MergeActionInstance(id, MergeAction.class.cast(action), action.getPredecessors().size());
        } else if (action instanceof SplitAction) {
            return new SplitActionInstance(id, SplitAction.class.cast(action), action.getSuccessors().size());
        } else if (action instanceof StartAction) {
            return new StartActionInstance(id, action);
        } else if (action instanceof EndAction) {
            return new EndActionInstance(id, action);
        } else {
            return new SimpleActionInstance(id, action);
        }
    }

    public void execute(WorkflowEngine engine, Arguments arguments, WorkflowEngineResultHandler result) {
        ActionInstance start = prepareExecute();

        print(start);

        engine.runWorkflow(this, start, arguments, result);
    }

    public void compensate(WorkflowEngine engine, String wid, WorkflowEngineResultHandler result) {
        ActionInstance start = prepareExecute();

        engine.compensateWorkflow(this, wid, start, result);
    }

    private ActionInstance prepareExecute() {
        insertMergeActions(getStartAction());
        insertSplitActions(getStartAction());
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
        if (a.getPredecessors().size() > 1 && !(a instanceof MergeAction)) {
            a.insertBefore(new MergeAction(this));
        }

        for (Action successor : a.getSuccessors()) {
            insertMergeActions(successor);
        }
    }

    private void insertSplitActions(Action a) {
        if (a.getSuccessors().size() > 1 && !(a instanceof SplitAction)) {
            a.insertAfter(new SplitAction(this));
        }

        for (Action successor : a.getSuccessors()) {
            insertSplitActions(successor);
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
