package io.yaas.workflow.runtime.execution;

import com.google.common.util.concurrent.SettableFuture;
import io.yaas.workflow.action.ActionResult;
import io.yaas.workflow.action.Arguments;
import io.yaas.workflow.runtime.ActionInstance;
import io.yaas.workflow.runtime.action.instance.WorkflowInstance;
import io.yaas.workflow.runtime.tracker.model.ActionBean;
import io.yaas.workflow.runtime.tracker.model.State;
import io.yaas.workflow.runtime.traversal.BackwardTraversal;
import io.yaas.workflow.runtime.traversal.TraversalStrategy;

/**
 * Created by D032705 on 30.04.2015.
 */
public class ForcedCompensationExecutor extends AbstractExecutor implements ExecutionStrategy {

    @Override
    public void execute(WorkflowInstance workflow, ActionInstance action, Arguments arguments, SettableFuture<ActionResult> result) {
        ActionInstance compensationActionInstance = action.getCompensationActionInstance();

        String wid = workflow.getId();
        String aid = compensationActionInstance.getId();

        // TODO !!!
        ActionBean lastCompenstationAction = workflow.getTrackingClient().getLastAction(wid, aid);
        if (lastCompenstationAction.astate != State.SUCCEEDED) {
            try {
                compensationActionInstance.execute(workflow, action.restore(workflow).getResult(), result);
            } catch (Exception e) {
                result.setException(e);
            }
        }
    }

    @Override
    public void start(WorkflowInstance workflow, ActionInstance action) {
        action.getCompensationActionInstance().start(workflow);
    }

    @Override
    public void success(WorkflowInstance workflow, ActionResult result, ActionInstance action) {
        action.getCompensationActionInstance().succeed(workflow, result);
    }

    @Override
    public void error(WorkflowInstance workflow, ActionInstance action, Arguments arguments, Throwable cause) {
        action.getCompensationActionInstance().error(workflow, cause);
    }

    public ExecutionStrategy getFallbackExecutionStrategy() {
        return new FailureExecutor();
    }

    @Override
    public TraversalStrategy getTraversalStrategy(ActionInstance action) {
        return BackwardTraversal.getInstance();
    }
}
