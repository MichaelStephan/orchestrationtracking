package io.yaas.workflow.action;

import io.yaas.workflow.Workflow;
import io.yaas.workflow.runtime.traversal.BackwardIterator;

import java.util.Iterator;

/**
 * Created by D032705 on 15.05.2015.
 */
public abstract class CompensationAction extends BaseAction {
    private Action action;

    public CompensationAction(ActionFunction f) {
        super(f);
    }

    public void setAction(Action action) {
        this.action = action;
    }
    public Action getAction() {
        return action;
    }

}
