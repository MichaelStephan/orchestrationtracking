package io.yaas.workflow.runtime;

import io.yaas.workflow.Action;
import io.yaas.workflow.ActionResult;
import io.yaas.workflow.Arguments;

import com.google.common.util.concurrent.SettableFuture;

abstract class ActionExecutor {
	
	protected Action _action;
	
	static ActionExecutor create(Action action) {
		if (action.getPredecessors().size() > 1)
			return new MergeActionExecutor(action);
		else
			return new SimpleActionExecutor(action);
	}
	
	protected ActionExecutor(Action action) {
		_action = action;
	}
	
	abstract void execute(Arguments arguments, SettableFuture<ActionResult> result);
}
