package io.yaas.workflow.runtime.tracker;

import io.yaas.workflow.Action;
import io.yaas.workflow.Workflow;
import io.yaas.workflow.runtime.WorkflowListener;
import io.yaas.workflow.runtime.tracker.client.WorkflowTrackingClient;
import io.yaas.workflow.runtime.tracker.model.ActionBean;
import io.yaas.workflow.runtime.tracker.model.State;
import io.yaas.workflow.runtime.tracker.model.WorkflowBean;

public class WorkflowTracker implements WorkflowListener {

	private WorkflowTrackingClient _trackingClient;
	
	public WorkflowTracker() {
    	this(System.getenv("TRACKER_SERVICE"));
	}

	public WorkflowTracker(String endpoint) {
    	_trackingClient = new WorkflowTrackingClient(endpoint);
	}
	
	@Override
	public void onWorkflowStart(Workflow workflow) {
    	_trackingClient.createWorkflow(new WorkflowBean(workflow));
	}

	@Override
	public void onWorkflowSuccess(Workflow workflow) {
		WorkflowBean workflowBean = new WorkflowBean(workflow);
		workflowBean.state  = State.SUCCEEDED;
    	_trackingClient.updateWorkflow(workflowBean);
	}

	@Override
	public void onWorkflowFailure(Workflow workflow, Throwable error) {
		WorkflowBean workflowBean = new WorkflowBean(workflow);
		workflowBean.state = State.FAILED;
    	_trackingClient.updateWorkflow(workflowBean);
	}

	@Override
	public void onWorkflowError(Workflow workflow, Throwable error) {
		WorkflowBean workflowBean = new WorkflowBean(workflow);
		workflowBean.state = State.FAILED;
    	_trackingClient.updateWorkflow(workflowBean);
	}

	@Override
	public void onWorkflowUnknown(Workflow workflow) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onActionStart(Action action) {
    	_trackingClient.createAction(new ActionBean(action));
	}

	@Override
	public void onActionSuccess(Action action) {
    	ActionBean actionBean = new ActionBean(action);
    	actionBean.state = State.SUCCEEDED;
    	_trackingClient.updateAction(actionBean);
	}

	@Override
	public void onActionFailure(Action action, Throwable error) {
    	ActionBean actionBean = new ActionBean(action);
    	actionBean.state = State.FAILED;
    	_trackingClient.updateAction(actionBean);
	}

	@Override
	public void onActionError(Action action, Throwable error) {
    	ActionBean actionBean = new ActionBean(action);
    	actionBean.state = State.FAILED;
    	_trackingClient.updateAction(actionBean);
	}

	@Override
	public void onActionUnknown(Action action) {
		// TODO Auto-generated method stub

	}

}
