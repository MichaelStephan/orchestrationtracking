package io.yaas.workflow.runtime.action.instance;

import io.yaas.workflow.action.ActionResult;
import io.yaas.workflow.runtime.ActionInstance;
import io.yaas.workflow.runtime.WorkflowInstance;

/**
 * Created by D032705 on 13.05.2015.
 */
public class StartCompensationActionInstance extends SimpleCompensationActionInstance {

    public StartCompensationActionInstance(ActionInstance actionInstance) {
        super(actionInstance);
    }

    @Override
    public ActionInstance getCompensationActionInstance() {
        return null;
    }

    @Override
    public void succeed(WorkflowInstance workflowInstance, ActionResult result) {
        super.succeed(workflowInstance, result);
        workflowInstance.compensated();
        workflowInstance.getResultHandler().compensated(workflowInstance);
    }
}
