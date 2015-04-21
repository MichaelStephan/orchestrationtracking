package io.yaas.workflow.runtime;

import io.yaas.workflow.Action;
import io.yaas.workflow.Workflow;

public interface WorkflowCallbackHandler {
	void onWorkflowStart(Workflow workflow);
	void onWorkflowSuccess(Workflow workflow);

	void onWorkflowFailure(Workflow workflow, Throwable error);
	void onWorkflowError(Workflow workflow, Throwable error);
	void onWorkflowUnknown(Workflow workflow);

	void onActionStart(Action action);
	void onActionSuccess(Action action);

	void onActionFailure(Action action, Throwable error);
	void onActionError(Action action, Throwable error);
	void onActionUnknown(Action action);

}
