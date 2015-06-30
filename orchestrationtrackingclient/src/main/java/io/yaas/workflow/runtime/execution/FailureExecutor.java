package io.yaas.workflow.runtime.execution;

import com.google.common.util.concurrent.SettableFuture;
import io.yaas.workflow.action.ActionResult;
import io.yaas.workflow.action.Arguments;
import io.yaas.workflow.runtime.ActionInstance;
import io.yaas.workflow.runtime.action.instance.WorkflowInstance;

import java.util.Collection;
import java.util.Collections;

/**
 * Created by i303874 on 6/29/15.
 */
public class FailureExecutor implements ExecutionStrategy {
    @Override
    public void start(WorkflowInstance workflow, ActionInstance action) {

    }

    @Override
    public void success(WorkflowInstance workflow, ActionResult result, ActionInstance action) {
        error(workflow, action, Arguments.EMPTY_ARGUMENTS, new UnknownError());
    }

    @Override
    public void error(WorkflowInstance workflow, ActionInstance action, Arguments arguments, Throwable cause) {
        workflow.error();
    }

    @Override
    public void execute(WorkflowInstance workflow, ActionInstance action, Arguments arguments, SettableFuture<ActionResult> result) {
        error(workflow, action, Arguments.EMPTY_ARGUMENTS, new UnknownError());
    }

    @Override
    public ExecutionStrategy getActionErrorStrategy() {
        return null;
    }

    @Override
    public Collection<ActionInstance> getNext(ActionInstance action) {
        return Collections.emptyList();
    }
}
