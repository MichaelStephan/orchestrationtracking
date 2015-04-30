package io.yaas.workflow.runtime.execution;

import com.google.common.util.concurrent.SettableFuture;
import io.yaas.workflow.ActionResult;
import io.yaas.workflow.Arguments;
import io.yaas.workflow.runtime.ActionInstance;
import io.yaas.workflow.runtime.action.instance.WorkflowInstance;

import java.util.Collection;

/**
 * Created by D032705 on 30.04.2015.
 */
public interface ExecutionStrategy {
    void start(WorkflowInstance workflow, ActionInstance action);
    void success(WorkflowInstance workflow, ActionInstance action);
    void error(WorkflowInstance workflow, ActionInstance action, Arguments arguments, Throwable cause);

    void execute(WorkflowInstance workflow, ActionInstance action, Arguments arguments, SettableFuture<ActionResult> result);

    Collection<ActionInstance> getNext(ActionInstance action);
}
