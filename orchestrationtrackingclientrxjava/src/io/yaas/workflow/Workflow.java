package io.yaas.workflow;

import java.util.HashMap;
import java.util.Map;

public class Workflow {
	
	private String _name;
	private String _version;
	
	private Action _startAction;
	
	private WorkflowExecutor _executor;
	
	private ErrorHandler _onFailureHandler;
	private ErrorHandler _onUnknownHandler;
	
	private Map<String, Action> _actionsRegistry = new HashMap<String, Action>();
	
	// @Inject
	public Workflow(String name, String version, WorkflowExecutor executor) {
		_name = name;
		_version = version;
		_executor = executor;
	}
	
	public String getName() {
		return _name;
	}

	public String getVersion() {
		return _version;
	}
	
	public String getDigest() {
		return ""; // TODO
	}

	public Workflow setErrorHandler(ErrorHandler onFailure, ErrorHandler onUnknown) {
		_onFailureHandler = onFailure;
		_onUnknownHandler = onUnknown;
		
		return this;
	}
	
	public ErrorHandler getOnFailure() {
		return _onFailureHandler;
	}
	
	public ErrorHandler getOnUnknown() {
		return _onUnknownHandler;
	}

	public Action getStartAction() {
		if (_startAction == null) {
			_startAction = new Action(this, null);
		}
		return _startAction;
	}
	
	public String generateId(Action a) {
		return ""; // TODO must be stable
	}
	
	public Action getAction(String id) {
		return _actionsRegistry.get(id); 
	}
	
	public void execute() {
		_executor.execute(this);
	}

	public String registerAction(Action a) {
		String id = generateId(a);
		_actionsRegistry.put(id, a);
		return id;
	}
}
