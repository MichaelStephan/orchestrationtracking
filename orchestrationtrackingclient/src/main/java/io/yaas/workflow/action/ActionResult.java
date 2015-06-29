package io.yaas.workflow.action;


/**
 * Created by i303874 on 3/11/15.
 */
public class ActionResult {
    private Arguments result;

    public ActionResult(Arguments result) {
        this.result = result;
    }

    public Arguments getResult() {
        return result;
    }
}
