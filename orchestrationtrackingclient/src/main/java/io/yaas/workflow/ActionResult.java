package io.yaas.workflow;


import io.yaas.workflow.runtime.ActionInstance;

/**
 * Created by i303874 on 3/11/15.
 */
public class ActionResult {
    private ActionInstance actionInstance;
    private Arguments result;

    public ActionResult(ActionInstance actionInstance, Arguments result) {
        this.actionInstance = actionInstance;
        this.result = result;
    }

    public ActionInstance getActionInstance() {
        return actionInstance;
    }

    public Arguments getResult() {
        return result;
    }
}
