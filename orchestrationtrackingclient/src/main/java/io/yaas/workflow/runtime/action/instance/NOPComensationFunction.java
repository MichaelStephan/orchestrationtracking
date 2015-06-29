package io.yaas.workflow.runtime.action.instance;

import io.yaas.workflow.action.ActionFunction;
import io.yaas.workflow.action.ActionResult;
import io.yaas.workflow.action.Arguments;

/**
 * Created by i303874 on 6/29/15.
 */
public class NOPComensationFunction implements ActionFunction {
    @Override
    public ActionResult apply(Arguments arguments) {
        return null;
    }
}
