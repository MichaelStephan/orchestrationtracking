package io.yaas.workflow;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class Workflow {
	
	private String _name;
	private String _version;
	
	private Action _startAction;
	
	private WorkflowExecutor _executor;
	
	private ErrorHandler _onFailureHandler;
	private ErrorHandler _onUnknownHandler;
	
	private Map<String, Action> _actionsRegistry = new HashMap<String, Action>();

	public static class Action {
		
		private String _name;
		private String _version;
		
		private Set<Action> _successors = new TreeSet<Action>(new Comparator<Action>() {
			@Override
			public int compare(Action o1, Action o2) {
				if (o1.getNameVersion() == null) return -1;
				else return o1.getNameVersion().compareTo(o2.getNameVersion());
			}
		});
		private Set<Action> _predecessors = new HashSet<Action>();
		
		private Workflow _w;
		
		private Body _body;
		
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
		
		private Action(String name, String version, Workflow w, Body f) {
			_name = name;
			_version = version;
			_w = w;
			_body = f;
		}
		public Action addAction(String name, String version, Body f) {
			Action successor = new Action(name, version, _w, f);
			addAction(successor);
			return successor;
		}
		
		public Action addAction(Action successor) {
			addSuccessor(successor);
			return successor;
		}

		public String getId() {
			return _id;
		}
		
		private void setId(String id) {
			_id = id;
		}
		
		public Action setErrorHandler(ErrorHandler onFailure, ErrorHandler onUnknown) {
			_onFailure = onFailure;
			_onUnknown = onUnknown;
			
			return this;
		}
		
		public Set<Action> getSuccessors() {
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
		
		public Set<Action> getPredecessors() {
			return _predecessors;
		}
		
		private void addSuccessor(Action successor) {
			_successors.add(successor);
			successor.addPredecessor(this);
		}
		
		private void addPredecessor(Action predecessor) {
			_predecessors.add(predecessor);
		}
		public String getName() {
			return _name;
		}
		public String getVersion() {
			return _version;
		}
		
	}
	
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
			_startAction = new Action("Start action", "0.0", this, (ignore) -> { return null; });
		}
		return _startAction;
	}
	
	private String generateId(Action a, int i) { // yes I know that synchronized methods are BAD :)
		return a.getName() + "." + a.getVersion() + "." + i;
	}
	
	public Action getAction(String id) {
		return _actionsRegistry.get(id); 
	}
	
	public void execute() {
		prepareExecute();
		_executor.execute(this);
	}

	private void prepareExecute() {
		List<Action> linearized = getLinearizedActions();
		for (int i = 0; i < linearized.size(); i++) {
			registerAction(linearized.get(i), i);
		}
	}
	
	private List<Action> getLinearizedActions() {
        List<Action> sorted = new LinkedList<Action>();
        Set<Action> visited = new HashSet<Action>();
        visitAction(visited, sorted, _startAction);
        return sorted;
	}

	private void visitAction(Set<Action> visited, List<Action> sorted, Action a) {
        for (Action successor: a.getSuccessors()) { 
            if (!visited.contains(successor)) {
            	sorted.add(successor);
            	visited.add(successor);
                visitAction(visited, sorted, successor);
            }
        }
	}
	
	private String registerAction(Action a, int i) {
		String id = generateId(a, i);
		_actionsRegistry.put(id, a);
		a.setId(id);
		return id;
	}
	
}