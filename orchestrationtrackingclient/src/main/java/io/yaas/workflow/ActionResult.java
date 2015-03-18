package io.yaas.workflow;

import io.yaas.workflow.Workflow.Action;

/**
 * Created by i303874 on 3/11/15.
 */
public class ActionResult {
    private Action action;
    private Arguments result;

    public ActionResult(Action action, Arguments result) {
        this.action = action;
        this.result = result;
    }

    public Action getAction() {
        return action;
    }

    public Arguments getResult() {
        return result;
    }
}
