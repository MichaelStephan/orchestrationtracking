package io.yaas.workflow.runtime;

import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.SettableFuture;

import java.util.Map;

/**
 * Created by i303874 on 3/11/15.
 */
public class WorkflowEngine {
    public WorkflowEngine() {
    }

    public void runAction(Action action, Map<String, Object> arguments) {
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
            action.getFunc().call(action, arguments, future);
        } catch (Exception e) {
            System.out.println(action.getName() + " - failed: " + e.getClass() + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}
