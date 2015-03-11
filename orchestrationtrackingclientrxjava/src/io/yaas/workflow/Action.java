package io.yaas.workflow;

import java.util.ArrayList;
import java.util.List;

public class Action {
	
	private List<Action> _successors = new ArrayList<Action>();
	
	private Workflow _w;
	
	private Body _body;
	
	private ErrorHandler _onFailure;
	private ErrorHandler _onUnknown;
	
	public Action(Workflow w, Body f) {
		_w = w;
		_body = f;
	}
	public Action addAction(Body f) {
		Action successor = new Action(_w, f);
		_w.registerAction(successor);
		_successors.add(successor);
		
		return successor;
	}
	
	public Action setErrorHandler(ErrorHandler onFailure, ErrorHandler onUnknown) {
		_onFailure = onFailure;
		_onUnknown = onUnknown;
		
		return this;
	}
	
	public List<Action> getSuccessors() {
		return _successors;
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
}
