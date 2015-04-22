package io.yaas.workflow.runtime.tracker;

import io.yaas.workflow.Action;
import io.yaas.workflow.Workflow;
import io.yaas.workflow.runtime.WorkflowCallbackHandler;
import io.yaas.workflow.runtime.tracker.client.WorkflowTrackingClient;
import io.yaas.workflow.runtime.tracker.model.ActionBean;
import io.yaas.workflow.runtime.tracker.model.State;
import io.yaas.workflow.runtime.tracker.model.WorkflowBean;

import java.util.HashMap;
import java.util.Map;

public class WorkflowTracker implements WorkflowCallbackHandler {

	private WorkflowTrackingClient _trackingClient;
	private Map<Workflow, WorkflowBean> _registry = new HashMap<Workflow, WorkflowBean>();
	
	public WorkflowTracker() {
    	this(System.getenv("TRACKER_SERVICE"));
	}

	public WorkflowTracker(String endpoint) {
    	_trackingClient = new WorkflowTrackingClient(endpoint);
	}
	
	@Override
	public void onWorkflowStart(Workflow workflow) {
		WorkflowBean bean = new WorkflowBean(workflow);
		_registry.put(workflow, bean);
    	_trackingClient.createWorkflow(bean);
	}

	@Override
	public void onWorkflowSuccess(Workflow workflow) {
		WorkflowBean workflowBean = _registry.get(workflow);
		workflowBean.wstate  = State.SUCCEEDED;
    	_trackingClient.updateWorkflow(workflowBean);
    	_registry.remove(workflow);
	}

	@Override
	public void onWorkflowFailure(Workflow workflow, Throwable error) {
		WorkflowBean workflowBean = _registry.get(workflow);
		workflowBean.wstate = State.FAILED;
    	_trackingClient.updateWorkflow(workflowBean);
	}

	@Override
	public void onWorkflowError(Workflow workflow, Throwable error) {
		WorkflowBean workflowBean = _registry.get(workflow);
		workflowBean.wstate = State.FAILED;
    	_trackingClient.updateWorkflow(workflowBean);
	}

	@Override
	public void onWorkflowUnknown(Workflow workflow) {
		WorkflowBean workflowBean = _registry.get(workflow);
	}

	@Override
	public void onActionStart(Action action) {
    	_trackingClient.createAction(new ActionBean(_registry.get(action.getWorkflow()), action));
	}

	@Override
	public void onActionSuccess(Action action) {
    	ActionBean actionBean = new ActionBean(_registry.get(action.getWorkflow()), action);
    	actionBean.astate = State.SUCCEEDED;
    	_trackingClient.updateAction(actionBean);
	}

	@Override
	public void onActionFailure(Action action, Throwable error) {
    	ActionBean actionBean = new ActionBean(_registry.get(action.getWorkflow()), action);
    	actionBean.astate = State.FAILED;
    	_trackingClient.updateAction(actionBean);
	}

	@Override
	public void onActionError(Action action, Throwable error) {
    	ActionBean actionBean = new ActionBean(_registry.get(action.getWorkflow()), action);
    	actionBean.astate = State.FAILED;
    	_trackingClient.updateAction(actionBean);
	}

	@Override
	public void onActionUnknown(Action action) {
		// TODO Auto-generated method stub

	}

}
