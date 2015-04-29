package io.yaas.workflow.runtime;

import io.yaas.workflow.Action;

/**
 * Created by D032705 on 28.04.2015.
 */
public class SimpleActionInstance extends ActionInstance {
    public SimpleActionInstance(Action action) {
        super(action);
    }
    public SimpleActionInstance(String wid, String aid, String timestamp, Action action) {
        super(wid, aid, timestamp, action);
    }

}
