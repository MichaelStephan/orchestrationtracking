package io.yaas.workflow;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class Action extends Node {
	
	private String _name;
	private String _version;

	private Workflow _w;
	
	protected Body _body;
	
	private ErrorHandler _onFailure;
	private ErrorHandler _onUnknown;
	
	private String _id;
	
	private String getNameVersion() {
		if (getName() == null && getVersion() == null)
			return null;
		else if (getName() == null)
			return getVersion();
		else
			return getName() + "." + getVersion();
	}
	
	public Action(String name, String version, Workflow w) {
		_name = name;
		_version = version;
		_w = w;
	}
	public Action(String name, String version) {
		_name = name;
		_version = version;
	}

	public Action(String name, String version, Body f) {
		_name = name;
		_version = version;
		_body = f;
	}
	
	public Action addAction(String name, String version, Body f) {
		Action successor = new Action(name, version, _w);
		successor.setFunction(f);
		addAction(successor);
		return successor;
	}
	
	public Action addAction(Action successor) {
		addSuccessor(successor);
		successor._w = this._w;
		return successor;
	}

	public Action setFunction(Body f) {
		if (_body != null)
			throw new IllegalStateException();
		_body = f;
		return this;
	}
	
	public String getId() {
		return _id;
	}
	
	void setId(String id) {
		_id = id;
	}
	
	public Action setErrorHandler(ErrorHandler onFailure, ErrorHandler onUnknown) {
		_onFailure = onFailure;
		_onUnknown = onUnknown;
		
		return this;
	}
	
	public Workflow getWorkflow() {
		return _w;
	}
	
	public void setWorkflow(Workflow w) {
		_w = w;
	}

	public Body getFunction() {
		return _body;
	}
	
	public ErrorHandler getOnFailure() {
		return _onFailure;
	}
	
	public ErrorHandler getOnUnknown() {
		return _onUnknown;
	}

	public String getName() {
		return _name;
	}
	
	public String getVersion() {
		return _version;
	}
	
    public String toString() {
        return "ACTION: " + getId() + ":" + getName() + " \nSUCCESSORS: " + getSuccessors().stream().map((action) -> {
            return action.getName();
        }).collect(Collectors.toList());
    }
    
}

