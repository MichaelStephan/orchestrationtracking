package io.yaas.workflow.runtime.action.instance;

import io.yaas.workflow.action.Action;
import io.yaas.workflow.runtime.ActionInstance;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by D032705 on 29.06.2015.
 */
public abstract class BaseActionInstance implements ActionInstance {

    protected Set<ActionInstance> predecessors = new HashSet<>();
    protected String id;
    protected Set<ActionInstance> successors = new HashSet<>();

    public void addSuccessor(ActionInstance action) {
        successors.add(checkNotNull(action));
    }


    @Override
    public void addPredecessor(ActionInstance action) {
        predecessors.add(checkNotNull(action));    }

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
    public ActionInstance copy(ActionInstance source) {
        return null;
    }

    @Override
    public Set<ActionInstance> getPredecessors() {
        return this.predecessors;
    }

}
