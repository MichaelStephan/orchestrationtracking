package io.yaas.workflow.runtime;

import io.yaas.workflow.Action;
import io.yaas.workflow.ActionResult;
import io.yaas.workflow.Arguments;

import com.google.common.util.concurrent.SettableFuture;

class SimpleActionExecutor extends ActionExecutor {
	
	public SimpleActionExecutor(Action action) {
		super(action);
	}

	@Override
	public void execute(Arguments arguments, SettableFuture<ActionResult> result) {
        new Thread(() -> {
        	result.set(_action.getFunction().apply(_action, arguments)); 
        }).start();
    }
}
