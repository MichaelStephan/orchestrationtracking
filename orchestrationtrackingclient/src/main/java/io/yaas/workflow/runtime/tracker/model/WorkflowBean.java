package io.yaas.workflow.runtime.tracker.model;

import io.yaas.workflow.Workflow;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class WorkflowBean {
	public String wid;
	public String name;
	public int version;
	public State wstate;
	
	public WorkflowBean() {}

	public WorkflowBean(String name, int version) {
		this.name = name;
		this.version = version;
		this.wstate = State.STARTED;
	}

	public WorkflowBean(Workflow w) {
		this(w.getName(), w.getVersion());
	}

}
