package io.yaas.workflow.runtime.traversal;

import io.yaas.workflow.action.Action;

import java.util.Collections;

/**
 * Created by D032705 on 26.06.2015.
 */
public class StopIterator extends ActionIterator {

    public StopIterator(Action action) {
        super(action);
        this.iterator = Collections.EMPTY_LIST.iterator();
    }
}
