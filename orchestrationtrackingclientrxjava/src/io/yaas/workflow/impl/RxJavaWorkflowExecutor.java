package io.yaas.workflow.impl;

import io.yaas.workflow.Workflow;
import io.yaas.workflow.Workflow.Action;
import io.yaas.workflow.WorkflowExecutor;

public class RxJavaWorkflowExecutor implements WorkflowExecutor {

	@Override
	public Object execute(Workflow w) {
		// TODO traverse starting from startAction, build a chain/subscribe
		return executeFrom(null, w.getStartAction());
	}

	@Override
	public Object executeFrom(Object input, Action a) {
		return null;
	}

	@Override
	public Object executeOnly(Object input, Action a) {
		return null;
	}

	
}
