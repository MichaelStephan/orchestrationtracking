package io.yaas.workflow.runtime.traversal;

import io.yaas.workflow.action.Action;

import java.util.Iterator;

/**
 * Created by D032705 on 15.05.2015.
 */
public abstract class ActionIterator implements Iterator<Action> {
    protected Action action;
    protected Iterator<Action> iterator;

    protected ActionIterator(Action action) {
        this.action = action;
    }

    @Override
    public boolean hasNext() {
        return iterator.hasNext();
    }

    @Override
    public Action next() {
        return iterator.next();
    }
}
