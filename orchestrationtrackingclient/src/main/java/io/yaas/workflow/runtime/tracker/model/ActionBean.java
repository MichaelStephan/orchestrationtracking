package io.yaas.workflow.runtime.tracker.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.yaas.workflow.Action;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ActionBean {
    public String aid;
    public String timestamp;
    public String wid;
    public String name;
    public String version;
    public State astate;

    public ActionBean() {
    }

    public ActionBean(String wid, String name, String version, String aid) {
        this.wid = wid;
        this.name = name;
        this.version = version;
        this.aid = aid;
        this.astate = State.STARTED;
    }

    public ActionBean(String wid, String name, String version, String aid, String timestamp) {
        this.wid = wid;
        this.name = name;
        this.version = version;
        this.aid = aid;
        this.astate = State.STARTED;
        this.timestamp = timestamp;
    }

    public ActionBean(WorkflowBean w, Action a) {
        this(w.wid, a.getName(), a.getVersion(), a.getId());
    }
}
