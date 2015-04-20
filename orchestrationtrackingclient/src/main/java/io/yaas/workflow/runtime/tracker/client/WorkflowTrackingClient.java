package io.yaas.workflow.runtime.tracker.client;

import io.yaas.workflow.Workflow;
import io.yaas.workflow.runtime.tracker.model.ActionBean;
import io.yaas.workflow.runtime.tracker.model.State;
import io.yaas.workflow.runtime.tracker.model.WorkflowBean;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;

public class WorkflowTrackingClient extends RestClient {
	//@Inject
	public WorkflowTrackingClient(String endpoint) {
		super(endpoint);
	}
	
	public WorkflowBean createWorkflow(WorkflowBean inputWorkflow) {
		WorkflowBean outputWorkflow = _resource
				.path("workflows")
				.request(MediaType.APPLICATION_JSON)
//				.accept(MediaType.APPLICATION_JSON)
				.post(Entity.entity(inputWorkflow, MediaType.APPLICATION_JSON), WorkflowBean.class);
	
		System.out.println(String.format(
		        "POST [%s] to [%s], status code [%d], returned data: "
		                + System.getProperty("line.separator") + "%s",
		        asString(inputWorkflow), _endpoint, 200, asString(outputWorkflow)));

		return outputWorkflow;
	}

	public void startWorkflow(Workflow workflow) {
		
	}
	
	public WorkflowBean updateWorkflow(WorkflowBean inputWorkflow) {
		WorkflowBean outputWorkflow = null;
		try {
			outputWorkflow = _resource
					.path("workflows")
					.path(String.valueOf(inputWorkflow.id))
					.request(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
					.put(Entity.entity(inputWorkflow, MediaType.APPLICATION_JSON), WorkflowBean.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		System.out.println(String.format(
		        "PUT [%s] to [%s], status code [%d], returned data: "
		                + System.getProperty("line.separator") + "%s",
		        asString(inputWorkflow), _endpoint, 200, asString(outputWorkflow)));

		return outputWorkflow;
	}

	public ActionBean createAction(ActionBean inputAction) {
		ActionBean outputAction = _resource
				.path("workflows")
				.path(String.valueOf(inputAction.workflow_id))
				.path("actions")
				.request(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.post(Entity.entity(inputAction, MediaType.APPLICATION_JSON), ActionBean.class);
	
		System.out.println(String.format(
		        "POST [%s] to [%s], status code [%d], returned data: "
		                + System.getProperty("line.separator") + "%s",
		        asString(inputAction), _endpoint, 200, asString(outputAction)));

		return outputAction;
	}

	public ActionBean updateAction(ActionBean inputAction) {
		ActionBean outputAction = null;
		try {
			outputAction = _resource
					.path("workflows")
					.path(String.valueOf(inputAction.workflow_id))
					.path("actions")
					.path(String.valueOf(inputAction.id))
					.request(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
					.put(Entity.entity(inputAction, MediaType.APPLICATION_JSON), ActionBean.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
	
		System.out.println(String.format(
		        "PUT [%s] to [%s], status code [%d], returned data: "
		                + System.getProperty("line.separator") + "%s",
		        asString(inputAction), _endpoint, 200, asString(outputAction)));

		return outputAction;
	}

	public static void main(String[] args) {
		
		WorkflowTrackingClient cli = new WorkflowTrackingClient("http://localhost:8080");
		WorkflowBean testWorkflow = new WorkflowBean("test1", 1);
		cli.createWorkflow(testWorkflow);
//		ActionBean testAction = new ActionBean();
		testWorkflow.state = State.SUCCEEDED;
		cli.updateWorkflow(testWorkflow);
		
	}
}
