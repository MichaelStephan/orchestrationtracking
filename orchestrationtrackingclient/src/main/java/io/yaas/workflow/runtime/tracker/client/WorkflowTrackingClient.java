package io.yaas.workflow.runtime.tracker.client;

import io.yaas.workflow.runtime.tracker.model.ActionBean;
import io.yaas.workflow.runtime.tracker.model.ResultBean;
import io.yaas.workflow.runtime.tracker.model.WorkflowBean;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.jackson.JacksonFeature;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import static com.google.common.base.Preconditions.checkNotNull;

public class WorkflowTrackingClient {

    private String _endpoint;
    private WebTarget _resource;

    public WorkflowTrackingClient(String endpoint) {
        _endpoint = checkNotNull(endpoint);

        final ClientConfig сс = new ClientConfig();
        сс.register(JacksonFeature.class);
        Client client = ClientBuilder.newClient(сс);
        _resource = client.target(_endpoint);
    }

    public WorkflowBean createWorkflow(WorkflowBean inputWorkflow) {
        try {
            return _resource
                    .path("workflows")
                    .request(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .post(Entity.entity(inputWorkflow, MediaType.APPLICATION_JSON), WorkflowBean.class);
        } catch (Exception e) {
            throw new WorkflowTrackingClientException("unable to create workflow", e);
        }
    }

    public WorkflowBean updateWorkflow(WorkflowBean inputWorkflow) {
        try {
            return _resource
                    .path("workflows")
                    .path(String.valueOf(inputWorkflow.wid))
                    .request(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .put(Entity.entity(inputWorkflow, MediaType.APPLICATION_JSON), WorkflowBean.class);
        } catch (Exception e) {
            throw new WorkflowTrackingClientException("unable to update workflow", e);
        }
    }

    public ActionBean createAction(ActionBean inputAction) {
        try {
            return _resource
                    .path("workflows")
                    .path(String.valueOf(inputAction.wid))
                    .path("actions")
                    .request(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .post(Entity.entity(inputAction, MediaType.APPLICATION_JSON), ActionBean.class);
        } catch (Exception e) {
            throw new WorkflowTrackingClientException("unable to create action", e);
        }
    }

    public ActionBean updateAction(ActionBean inputAction) {
        try {
            return _resource
                    .path("workflows")
                    .path(String.valueOf(inputAction.wid))
                    .path("actions")
                    .path(String.valueOf(inputAction.aid))
                    .path(String.valueOf(inputAction.timestamp))
                    .request(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .put(Entity.entity(inputAction, MediaType.APPLICATION_JSON), ActionBean.class);
        } catch (Exception e) {
            throw new WorkflowTrackingClientException("unable to update action", e);
        }
    }

    public ActionBean getLastAction(String wid, String aid) {
        try {
            return _resource
                    .path("workflows")
                    .path(String.valueOf(wid))
                    .path("actions")
                    .path(String.valueOf(aid))
                    .path("last")
                    .request(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("content-type", "application/json")
                    .get().readEntity(ActionBean.class);
        } catch (Exception e) {
            throw new WorkflowTrackingClientException("unable to get last action", e);
        }
    }

    public ResultBean getLastActionData(String wid, String aid) {
        try {
            return _resource
                    .path("workflows")
                    .path(String.valueOf(wid))
                    .path("actions")
                    .path(String.valueOf(aid))
                    .path("last")
                    .path("data")
                    .request(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("content-type", "application/json")
                    .get().readEntity(ResultBean.class);
        } catch (Exception e) {
            throw new WorkflowTrackingClientException("unable to get last action data", e);
        }
    }
}
