package io.yaas.workflow;

import java.util.Map;

/**
 * Created by i303874 on 3/11/15.
 */
public class ActionResult {
    private Action action;

    private Map<String, Object> result;

    public ActionResult(Action action, Map<String, Object> result) {
        this.action = action;
        this.result = result;
    }

    public Action getAction() {
        return action;
    }

    public Map<String, Object> getResult() {
        return result;
    }
}
