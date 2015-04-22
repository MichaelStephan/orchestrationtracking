package io.yaas.workflow.runtime.tracker.client;

import io.yaas.workflow.runtime.tracker.model.ActionBean;
import io.yaas.workflow.runtime.tracker.model.State;
import io.yaas.workflow.runtime.tracker.model.WorkflowBean;

import javax.ws.rs.ProcessingException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.jackson.JacksonFeature;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class WorkflowTrackingClient {

	private String _endpoint;
	private WebTarget _resource;

	public WorkflowTrackingClient(String endpoint) {
		_endpoint = endpoint;
		
        final ClientConfig сс = new ClientConfig();
        сс.register(JacksonFeature.class);
        Client client = ClientBuilder.newClient(сс);
        _resource = client.target(_endpoint);
	}
	
	protected String asString(Object b) {
		try {
			ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
			return ow.writeValueAsString(b);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			return "";
		}
	}
	public WorkflowBean createWorkflow(WorkflowBean inputWorkflow) {
		WorkflowBean outputWorkflow = null;
		try {
			outputWorkflow = _resource
				.path("workflows")
				.request(MediaType.APPLICATION_JSON)
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
				.path(String.valueOf(inputWorkflow.wid))
				.request(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.put(Entity.entity(inputWorkflow, MediaType.APPLICATION_JSON), WorkflowBean.class);

			System.out.println(String.format(
		        "PUT [%s] to [%s], status code [%d], returned data: "
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
				.path(String.valueOf(inputAction.wid))
				.path("actions")
				.request(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.post(Entity.entity(inputAction, MediaType.APPLICATION_JSON), ActionBean.class);
	
		System.out.println(String.format(
		        "POST [%s] to [%s], status code [%d], returned data: "
		                + System.getProperty("line.separator") + "%s",
		        asString(inputAction), _endpoint, 200, asString(outputAction)));
		} catch (WebApplicationException e) {
			System.out.println(String.format(
		        "POST [%s] to [%s] failed, status code [%d]",
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
				.path(String.valueOf(inputAction.wid))
				.path("actions")
				.path(String.valueOf(inputAction.aid))
				.request(MediaType.APPLICATION_JSON)
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
		WorkflowBean testWorkflow = new WorkflowBean("workflow_" + System.currentTimeMillis(), 1);
		testWorkflow = cli.createWorkflow(testWorkflow);
		ActionBean testAction = new ActionBean(testWorkflow.wid, "action_" + System.currentTimeMillis(), "1", String.valueOf(System.currentTimeMillis()));
		cli.createAction(testAction);
		testAction.astate = State.FAILED;
		cli.updateAction(testAction);
		testWorkflow.wstate = State.SUCCEEDED;
		cli.updateWorkflow(testWorkflow);		
	}
}
