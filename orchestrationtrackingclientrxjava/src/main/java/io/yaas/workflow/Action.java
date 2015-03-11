package io.yaas.workflow;

import com.google.common.collect.ImmutableSet;
import com.google.common.util.concurrent.SettableFuture;
import rx.functions.Func3;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * Created by i303874 on 3/11/15.
 */
public class Action {
    private String id;

    private String name;

    private Func3<Action, Map<String, Object>, SettableFuture<ActionResult>, Future<ActionResult>> func;

    private Set<Action> successors;

    public Action(String id, String name, Func3<Action, Map<String, Object>, SettableFuture<ActionResult>, Future<ActionResult>> func, List<Action> successors) {
        this.id = id;
        this.name = name;
        this.func = func;
        this.successors = ImmutableSet.copyOf(successors);
    }

    public Action(String id, String name, List<Action> successors) {
        this.id = id;
        this.name = name;
        this.successors = ImmutableSet.copyOf(successors);
    }

    public void setFunc(Func3<Action, Map<String, Object>, SettableFuture<ActionResult>, Future<ActionResult>> func) {
        if (this.func != null) {
            throw new IllegalStateException();
        }
        this.func = func;
    }

    public Func3<Action, Map<String, Object>, SettableFuture<ActionResult>, Future<ActionResult>> getFunc() {
        return func;
    }

    public Set<Action> getSuccessors() {
        return successors;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String toString() {
        return "ACTION: " + getId() + ":" + getName() + " \nSUCCESSORS: " + getSuccessors().stream().map((action) -> {
            return action.getName();
        }).collect(Collectors.toList());
    }
}
