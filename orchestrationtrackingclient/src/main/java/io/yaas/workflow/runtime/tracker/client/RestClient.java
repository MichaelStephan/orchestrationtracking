package io.yaas.workflow.runtime.tracker.client;

import io.yaas.workflow.runtime.tracker.model.EntityBean;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.jackson.JacksonFeature;

class RestClient {

	protected final String _endpoint;
	protected final WebTarget _resource;

	protected RestClient(String endpoint) {
		super();
		_endpoint = endpoint;
		
        final ClientConfig сс = new ClientConfig();
        сс.register(JacksonFeature.class);
        Client client = ClientBuilder.newClient(сс);
        _resource = client.target(_endpoint);

 	}
	
	protected String asString(EntityBean b) {
		return "";
	}
}