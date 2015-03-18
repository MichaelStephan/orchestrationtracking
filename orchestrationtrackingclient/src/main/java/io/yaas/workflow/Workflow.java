package io.yaas.workflow;

import io.yaas.workflow.runtime.WorkflowEngine;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class Workflow {
	
	private String _name;
	private String _version;
	
	private Action _startAction;
	
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
		
		private Action(String name, String version, Workflow w) {
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
		
		private void removeSuccessor(Action successor) {
			_successors.remove(successor);
			successor.removePredecessor(this);
		}
		
		private void addPredecessor(Action predecessor) {
			_predecessors.add(predecessor);
		}
		
		private void removePredecessor(Action predecessor) {
			_predecessors.remove(predecessor);
		}
		
		private void insertAfter(Action successor) {
			for (Action a: getSuccessors()) {
				successor.addSuccessor(a);
				removeSuccessor(a);
			}
			addSuccessor(successor);
		}
		
		private void insertBefore(Action successor) {
			List<Action> predecessors = new ArrayList<Action>();
			for (Action a: getPredecessors()) { // otherwise concurrent modification
				predecessors.add(a);
			}
			for (Action a: predecessors) {
				a.addSuccessor(successor);
				a.removeSuccessor(this);
			}
			successor.addSuccessor(this);
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
	
	// @Inject
	public Workflow(String name, String version) {
		_name = name;
		_version = version;
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
			_startAction = new Action("Start action", "0.0", (action, arguments, result) -> {
				result.set(new ActionResult(action, new Arguments(arguments)));
				return result;
			});
		}
		return _startAction;
	}
	
//	public Action setStartAction(Action a) {
//		_startAction = a;
//		return _startAction;
//	}
	
	private String generateId(Action a, int i) { 
		return a.getName() + "." + a.getVersion() + "." + i;
	}
	
	public Action getAction(String id) {
		return _actionsRegistry.get(id); 
	}
	
	public void execute(WorkflowEngine engine, Arguments arguments) {
		prepareExecute();
		engine.runAction(getStartAction(), arguments);
	}

	private void prepareExecute() {
		insertMergeActions();
		List<Action> linearized = getLinearizedActions();
		for (int i = 0; i < linearized.size(); i++) {
			registerAction(linearized.get(i), i);
		}
	}
	
	private void insertMergeActions() {
		mergeAction(getStartAction());
	}

	private void mergeAction(Action a) {
        for (Action successor: a.getSuccessors()) {
        	int count = a.getPredecessors().size();
            if (count > 1 && !(a instanceof MergeAction)) {
            	a.insertBefore(new MergeAction(count));
            }
       		mergeAction(successor);
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