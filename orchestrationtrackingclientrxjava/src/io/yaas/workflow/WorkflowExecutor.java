package io.yaas.workflow;

import io.yaas.workflow.Workflow.Action;

public interface WorkflowExecutor {
	Object execute(Workflow w);
	Object executeOnly(Object input, Action a);
	Object executeFrom(Object input, Action a);
}
