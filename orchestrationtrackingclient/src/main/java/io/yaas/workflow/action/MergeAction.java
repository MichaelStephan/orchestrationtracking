package io.yaas.workflow.action;


import io.yaas.workflow.Workflow;

/**
 * Created by i303874 on 3/11/15.
 * Basically it is a pure technology entity
 */
public class MergeAction extends SimpleAction {

    public MergeAction(Workflow w) {
        super("merge", "1.0", w);
    }

}
