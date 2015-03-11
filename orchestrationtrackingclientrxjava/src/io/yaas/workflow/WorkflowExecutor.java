package io.yaas.workflow;

public interface WorkflowExecutor {
	Object execute(Workflow w);
	Object executeOnly(Object input, Action a);
	Object executeFrom(Object input, Action a);
}
