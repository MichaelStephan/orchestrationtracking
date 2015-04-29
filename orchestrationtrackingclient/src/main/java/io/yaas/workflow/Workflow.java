package io.yaas.workflow;

import io.yaas.workflow.runtime.ActionInstance;
import io.yaas.workflow.runtime.MergeActionInstance;
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

    public Action addAction(Action a) {
        return getStartAction().addAction(a);
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

//    private void print(Action action) {
//        System.out.println("------");
//        System.out.println(action.hashCode());
//        System.out.println(action);
//        for (Action s : action.getSuccessors()) {
//            print(s);
//        }
//    }

    public void execute(WorkflowEngine engine, Arguments arguments) {
        prepareExecute();

//        print(getStartAction());

        engine.runWorkflow(this, arguments);
    }

    private ActionInstance prepareExecute() {
        return buildExecutionGraph(getStartAction());
//        insertMergeActions();
//        List<Action> linearized = getLinearizedActions();
//        for (int i = 0; i < linearized.size(); i++) {
//            registerAction(linearized.get(i), i);
//        }
    }

    private void insertMergeActions() {
        mergeAction(getStartAction());
    }

    private void mergeAction(Node a) {
        for (Node successor : a.getSuccessors()) {
            if (a.getPredecessors().size() > 1 && !(a instanceof MergeAction)) {
                a.insertBefore(new MergeActionInstance(this));
            }
            mergeAction(successor);
        }
    }

    private ActionInstance buildExecutionGraph(Action start) {
        return new NodeVisitor() {
            List<Node> instances = new LinkedList<Node>();
            Set<Node> visited = new HashSet<>();

            ActionInstance build(Action start) {
                start.accept(this);
                return (ActionInstance)instances.get(0);
            }

            @Override
            public void visit(Node node) {
                Action action = (Action) node;
                if (!visited.contains(action)) { // cycles processing
                    ActionInstance actionInstance = new ActionInstance(action);
                    instances.add(actionInstance);
                    // TODO predecessors
                    visited.add(action);
                    action.getSuccessors().stream().forEach((successor) -> {
                        successor.accept(this);
                    });
                }
            }
        }.build(start);
    }

    private String registerAction(Action a, int i) {
        String id = generateId(a, i);
        _actionsRegistry.put(id, a);
        a.setId(id);
        return id;
    }

}
