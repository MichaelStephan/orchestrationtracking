package io.yaas.workflow.runtime.tracker.client;

import io.yaas.workflow.runtime.tracker.model.ActionBean;
import io.yaas.workflow.runtime.tracker.model.State;
import io.yaas.workflow.runtime.tracker.model.WorkflowBean;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;

public class WorkflowTrackingClient extends RestClient {
	//@Inject
	public WorkflowTrackingClient(String endpoint) {
		super(endpoint);
	}
	
	public WorkflowBean createWorkflow(WorkflowBean inputWorkflow) {
		WorkflowBean outputWorkflow = null;
		try {
			outputWorkflow = _resource
				.path("workflows")
				.request()
				.accept(MediaType.APPLICATION_JSON)
				.post(Entity.entity(inputWorkflow, MediaType.APPLICATION_JSON), WorkflowBean.class);
	
			System.out.println(String.format(
		        "POST [%s] to [%s], status code [%d], returned data: "
		                + System.getProperty("line.separator") + "%s",
		        asString(inputWorkflow), _endpoint, 201, asString(outputWorkflow)));
		} catch (WebApplicationException e) {
			System.out.println(String.format(
		        "POST [%s] to [%s] failed, status code [%d]",
		        asString(inputWorkflow), _endpoint, e.getResponse().getStatus()));
		} catch (ProcessingException e) {
			System.out.println("Request processing exception");
			e.printStackTrace();
		}
		return outputWorkflow;
	}

	public WorkflowBean updateWorkflow(WorkflowBean inputWorkflow) {
		WorkflowBean outputWorkflow = null;
		try {
			outputWorkflow = _resource
				.path("workflows")
				.path(String.valueOf(inputWorkflow.id))
				.request()
				.accept(MediaType.APPLICATION_JSON)
				.put(Entity.entity(inputWorkflow, MediaType.APPLICATION_JSON), WorkflowBean.class);

			System.out.println(String.format(
		        "POST [%s] to [%s], status code [%d], returned data: "
		                + System.getProperty("line.separator") + "%s",
		        asString(inputWorkflow), _endpoint, 201, asString(outputWorkflow)));
		} catch (WebApplicationException e) {
			System.out.println(String.format(
		        "PUT [%s] to [%s] failed, status code [%d]",
		        asString(inputWorkflow), _endpoint, e.getResponse().getStatus()));
		} catch (ProcessingException e) {
			System.out.println("Request processing failed");
			e.printStackTrace();
		}
		return outputWorkflow;
	}

	public ActionBean createAction(ActionBean inputAction) {
		ActionBean outputAction = null;
		try {
			outputAction = _resource
				.path("workflows")
				.path(String.valueOf(inputAction.workflow_id))
				.path("actions")
				.request()
				.accept(MediaType.APPLICATION_JSON)
				.post(Entity.entity(inputAction, MediaType.APPLICATION_JSON), ActionBean.class);
	
		System.out.println(String.format(
		        "POST [%s] to [%s], status code [%d], returned data: "
		                + System.getProperty("line.separator") + "%s",
		        asString(inputAction), _endpoint, 200, asString(outputAction)));
		} catch (WebApplicationException e) {
			System.out.println(String.format(
		        "PUT [%s] to [%s] failed, status code [%d]",
		        asString(inputAction), _endpoint, e.getResponse().getStatus()));
		} catch (ProcessingException e) {
			System.out.println("Request processing failed");
			e.printStackTrace();
		}
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
				.request()
				.accept(MediaType.APPLICATION_JSON)
				.put(Entity.entity(inputAction, MediaType.APPLICATION_JSON), ActionBean.class);
			System.out.println(String.format(
		        "PUT [%s] to [%s], status code [%d], returned data: "
		                + System.getProperty("line.separator") + "%s",
		        asString(inputAction), _endpoint, 200, asString(outputAction)));
		} catch (WebApplicationException e) {
			System.out.println(String.format(
		        "PUT [%s] to [%s] failed, status code [%d]",
		        asString(inputAction), _endpoint, e.getResponse().getStatus()));
		} catch (ProcessingException e) {
			System.out.println("Request processing exception");
			e.printStackTrace();
		}
		return outputAction;
	}

	public static void main(String[] args) {
		
		WorkflowTrackingClient cli = new WorkflowTrackingClient("http://localhost:8080");
		WorkflowBean testWorkflow = new WorkflowBean("test" + System.currentTimeMillis(), 1);
		cli.createWorkflow(testWorkflow);
//		ActionBean testAction = new ActionBean();
		testWorkflow.state = State.SUCCEEDED;
		cli.updateWorkflow(testWorkflow);
		
	}
}
