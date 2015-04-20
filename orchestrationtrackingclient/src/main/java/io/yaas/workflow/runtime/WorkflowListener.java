package io.yaas.workflow.runtime;

import io.yaas.workflow.Action;
import io.yaas.workflow.Workflow;

public interface WorkflowListener {
	void onWorkflowStart(Workflow workflow);
	void onWorkflowSuccess(Workflow workflow);

	void onWorkflowFailure(Workflow workflow, Throwable t);
	void onWorkflowError(Workflow workflow, Throwable t);
	void onWorkflowUnknown(Workflow workflow);

	void onActionStart(Action action);
	void onActionSuccess(Action action);

	void onActionFailure(Action action, Throwable error);
	void onActionError(Action action, Throwable error);
	void onActionUnknown(Action action);

}
