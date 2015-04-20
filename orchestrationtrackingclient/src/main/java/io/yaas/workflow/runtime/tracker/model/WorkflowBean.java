package io.yaas.workflow.runtime.tracker.model;

import io.yaas.workflow.Workflow;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class WorkflowBean extends EntityBean {
	public String name;
	public int version;
	public State state;
	

	public WorkflowBean(String name, int version) {
		this.name = name;
		this.version = version;
	}
	
	public WorkflowBean(Workflow w) {
		this(w.getName(), w.getVersion());
		state = State.STARTED;
	}

}
