package io.yaas.workflow.runtime;

import com.google.common.util.concurrent.SettableFuture;
import io.yaas.workflow.Action;
import io.yaas.workflow.ActionResult;
import io.yaas.workflow.Arguments;
import io.yaas.workflow.Node;

/**
 * Created by i303874 on 4/28/15.
 */
public class ActionInstance extends Node {
    protected String wid;
    protected String aid;
    protected String timestamp;
    protected Action action;

    public ActionInstance(Action action) {
        this.action = action;
    }
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

    public void setAid(String aid) {
        this.aid = aid;
    }

    public String getWid() {
        return wid;
    }

//    public void execute(ActionExecutor executor, Arguments arguments, SettableFuture<ActionResult> result) {
//        executor.execute(this, arguments, result);
//    }
}
