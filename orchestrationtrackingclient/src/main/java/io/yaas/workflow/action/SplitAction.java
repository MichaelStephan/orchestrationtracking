package io.yaas.workflow.action;


import io.yaas.workflow.Workflow;

/**
 * Created by i303874 on 3/11/15.
 * Basically it is a pure technology entity
 */
public class SplitAction extends SimpleAction {

    public SplitAction(Workflow w) {
        super("split", "1.0", w);
    }

}
