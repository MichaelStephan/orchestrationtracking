package io.yaas.workflow.runtime;

import io.yaas.workflow.Action;

/**
 * Created by i303874 on 4/28/15.
 */
public class ActionInstance {
    private String wid;

    private String aid;

    private String timestamp;

    private Action action;

    public ActionInstance(String wid, String aid, String timestamp, Action action) {
        this.aid = aid;
        this.wid = wid;
        this.timestamp = timestamp;
        this.action = action;
    }

    public Action getAction() {
        return action;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getAid() {
        return aid;
    }

    public String getWid() {
        return wid;
    }
}
