package io.yaas.workflow.impl;

import io.yaas.workflow.Action;
import io.yaas.workflow.Workflow;
import io.yaas.workflow.WorkflowExecutor;

import java.util.ArrayList;
import java.util.List;

public class PlainJava8WorkflowExecutor implements WorkflowExecutor {

	@Override
	public Object execute(Workflow w) {
		return executeFrom(null, w.getStartAction());
	}

	@Override
	public Object executeFrom(Object input,  Action a) {
		Object output = executeOnly(input, a);
		return executeSuccessors(output, a);
	}

	@Override
	public Object executeOnly(Object input, Action a) {
		return a.getFunction().apply(input);
	}
	
	private Object executeSuccessors(Object input, Action a) {
		List<Object> merged = new ArrayList<Object>();
		for (Action successor: a.getSuccessors() ) {
			merged.add(executeOnly(input, successor));
		}
		return merged;
	}
	
}
