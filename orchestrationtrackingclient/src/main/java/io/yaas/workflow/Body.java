package io.yaas.workflow;

import java.util.function.BiFunction;

public interface Body extends BiFunction<Action, Arguments, ActionResult> {

}
