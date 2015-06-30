package io.yaas.workflow.action;

import io.yaas.workflow.Workflow;
import io.yaas.workflow.graph.BaseNode;

import java.util.stream.Collectors;

/**
 * Created by D032705 on 15.05.2015.
 */
abstract class BaseAction extends BaseNode<Action> implements Action {

    // protected Node node;

    protected String name;
    protected String version;
    protected Workflow workflow;

    protected ActionFunction actionFunction = new NopFunction();
    protected ActionFunction compensationFunction = new NopFunction();

    public BaseAction(String name, String version, Workflow w) {
        super(getId(name, version));
        this.name = name;
        this.version = version;
        workflow = w;
    }

    public BaseAction(String name, String version) {
        super(getId(name, version));
        this.name = name;
        this.version = version;
    }

    public BaseAction(String name, String version, ActionFunction f) {
        super(getId(name, version));
        this.name = name;
        this.version = version;
        actionFunction = f;
    }

    private String getNameVersion() {
        return getId(getName(), getVersion());
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

    public static String getId(String name, String version) {
        if (name == null && version == null)
            return null;
        else if (name == null)
            return version;
        else
            return name + "." + version;
    }

    public String toString() {
        return getName() + " \nSUCCESSORS: " + getSuccessors().stream().map((action) -> {
            return action.getName();
        }).collect(Collectors.toList());
    }
}
