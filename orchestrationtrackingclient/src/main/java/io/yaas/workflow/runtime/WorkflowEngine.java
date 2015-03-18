package io.yaas.workflow.runtime;

import io.yaas.workflow.ActionResult;
import io.yaas.workflow.Arguments;
import io.yaas.workflow.Workflow;
import io.yaas.workflow.Workflow.Action;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.SettableFuture;

/**
 * Created by i303874 on 3/11/15.
 */
public class WorkflowEngine {
    public WorkflowEngine() {
    }

    public void runWorkflow(Workflow w, Arguments arguments) {
    	runAction(w.getStartAction(), arguments);
    }
    public void runAction(Action action, Arguments arguments) {
        SettableFuture<ActionResult> future = SettableFuture.create();
        Futures.addCallback(future, new FutureCallback<ActionResult>() {
            @Override
            public void onSuccess(ActionResult result) {
                System.out.println(action.getName() + " - succeeded");
                action.getSuccessors().forEach((successor) -> {
                    runAction(successor, result.getResult());
                });
            }

            @Override
            public void onFailure(Throwable error) {
                System.out.println(action.getName() + " - failed: " + error.getClass() + ": " + error.getMessage());
            }
        });

        System.out.println(action.getName() + " - started");
        try {
            action.getFunction().call(action, arguments, future);
        } catch (Exception e) {
            System.out.println(action.getName() + " - failed: " + e.getClass() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}
