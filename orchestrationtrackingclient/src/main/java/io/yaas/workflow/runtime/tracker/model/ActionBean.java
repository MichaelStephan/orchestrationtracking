package io.yaas.workflow.runtime.tracker.model;

import io.yaas.workflow.Action;

public class ActionBean extends EntityBean {
	public String workflow_id;
	public State state;
	
	public ActionBean(Action a) {
		workflow_id = new WorkflowBean(a.getWorkflow()).id;
		state = State.STARTED;
	}
}
