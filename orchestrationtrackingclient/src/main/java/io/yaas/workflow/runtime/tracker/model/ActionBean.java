package io.yaas.workflow.runtime.tracker.model;

import io.yaas.workflow.Action;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ActionBean {
	public String aid;
	public String wid;
	public String name;
	public String version;
	public State astate;
	
	public ActionBean() {}
	
	public ActionBean(String wid,String name, String version, String aid) {
		this.wid = wid;
		this.name = name;
		this.version = version;
		this.aid = aid;
		this.astate = State.STARTED;
	}

	public ActionBean(WorkflowBean w, Action a) {
		this(w.wid, a.getName(), a.getVersion(), a.getId());
	}
}
