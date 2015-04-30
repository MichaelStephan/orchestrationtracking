package io.yaas.workflow.runtime.tracker.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ActionBean {
    public String aid;
    public String timestamp;
    public String wid;
    public String name;
    public String version;
    public State astate;
    public Map<String, Object> data;

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
}
