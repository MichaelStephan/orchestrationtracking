package io.yaas.workflow.runtime;

import io.yaas.workflow.Action;
import io.yaas.workflow.ActionResult;
import io.yaas.workflow.Arguments;
import io.yaas.workflow.Workflow;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.SettableFuture;

/**
 * Created by i303874 on 3/11/15.
 */
public class WorkflowEngine {
	
	private Map<Action, ActionExecutor> _executors = new TreeMap<Action, ActionExecutor>(new Comparator<Action>() {

		@Override
		public int compare(Action o1, Action o2) {
			return o1 == o2 ? 0 : 1;
		}
		
	});
	
    public WorkflowEngine() {
    }

    public void runWorkflow(Workflow w, Arguments arguments) {
    	runAction(w.getStartAction(), arguments);
    }
    
    public void runAction(Action action, Arguments arguments) {
    	
    	// TODO factory
    	ActionExecutor executor = getExecutor(action);
        SettableFuture<ActionResult> future = SettableFuture.create();
        Futures.addCallback(future, new FutureCallback<ActionResult>() {
            @Override
            public void onSuccess(ActionResult result) {
            	after(action);
            	action.getSuccessors().forEach((successor) -> {
                    runAction(successor, result.getResult());
                });
            }

            @Override
            public void onFailure(Throwable error) {
            	error(action, error);
            }
        });
        before(action);
        try {
        	executor.execute(arguments, future);
        } catch (Exception e) {
        	failure(action, e);
        }
    }
    
    private ActionExecutor getExecutor(Action action) {
    	ActionExecutor executor = _executors.get(action);
    	if (executor == null) {
    		executor = ActionExecutor.create(action);
    		_executors.put(action, executor);
    	}
    	return executor;
    }
    private void before(Action action) {
    	// TODO call orchestration tracking
        System.out.println(action.getName() + " - started");
    }
    
    private void after(Action action) {
    	// TODO call orchestration tracking
        System.out.println(action.getName() + " - succeeded");
    }
    
    // application error
    private void error(Action action, Throwable error) {
    	// TODO call orchestration tracking
    	if (action.getOnFailure() != null)
    		action.getOnFailure().accept(error);
    	// TODO when to call onUnknown?o
        System.out.println(action.getName() + " - failed: " + error.getClass() + ": " + error.getMessage());
    }
    
    // technical error
    private void failure(Action action, Throwable error) {
    	// TODO call orchestration tracking
        System.out.println(action.getName() + " - failed: " + error.getClass() + ": " + error.getMessage());
        error.printStackTrace();
    }
}
