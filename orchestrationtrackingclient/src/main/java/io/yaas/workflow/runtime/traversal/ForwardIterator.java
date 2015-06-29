package io.yaas.workflow.runtime.traversal;

import io.yaas.workflow.action.Action;

/**
 * Created by D032705 on 15.05.2015.
 */
public class ForwardIterator extends ActionIterator {

    public ForwardIterator(Action action) {
        super(action);
        this.iterator = action.getSuccessors().iterator();
    }
}
