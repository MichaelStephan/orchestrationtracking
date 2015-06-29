package io.yaas.workflow.runtime.traversal;

import io.yaas.workflow.action.Action;

/**
 * Created by D032705 on 26.06.2015.
 */
public class BackwardIterator extends ActionIterator {

        public BackwardIterator(Action action) {
            super(action);
            this.iterator = action.getPredecessors().iterator();
        }
}
