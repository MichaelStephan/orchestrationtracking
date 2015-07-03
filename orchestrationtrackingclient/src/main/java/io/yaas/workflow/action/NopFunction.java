package io.yaas.workflow.action;

import io.yaas.workflow.action.ActionFunction;
import io.yaas.workflow.action.ActionResult;
import io.yaas.workflow.action.Arguments;

/**
 * Created by i303874 on 6/29/15.
 */
public class NopFunction implements ActionFunction {
    @Override
    public ActionResult apply(Arguments arguments) {
        return ActionResult.EMPTY_RESULT;
    }
}
