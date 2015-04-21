package io.yaas.workflow.runtime;

import io.yaas.workflow.Action;
import io.yaas.workflow.Workflow;

public class UserCallbacksHandler implements WorkflowCallbackHandler {

	@Override
	public void onWorkflowStart(Workflow workflow) {
	}

	@Override
	public void onWorkflowSuccess(Workflow workflow) {
	}

	@Override
	public void onWorkflowFailure(Workflow workflow, Throwable error) {
    	if (workflow.getOnFailure() != null)
    		workflow.getOnFailure().accept(error);
	}

	@Override
	public void onWorkflowError(Workflow workflow, Throwable error) {
    	if (workflow.getOnFailure() != null)
    		workflow.getOnFailure().accept(error);
	}

	@Override
	public void onWorkflowUnknown(Workflow workflow) {
    	if (workflow.getOnUnknown() != null)
    		workflow.getOnUnknown();
	}

	@Override
	public void onActionStart(Action action) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onActionSuccess(Action action) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onActionFailure(Action action, Throwable error) {
    	if (action.getOnFailure() != null)
    		action.getOnFailure().accept(error);
	}

	@Override
	public void onActionError(Action action, Throwable error) {
    	if (action.getOnFailure() != null)
    		action.getOnFailure().accept(error);
	}

	@Override
	public void onActionUnknown(Action action) {
    	if (action.getOnUnknown() != null)
    		action.getOnUnknown();
	}

}
