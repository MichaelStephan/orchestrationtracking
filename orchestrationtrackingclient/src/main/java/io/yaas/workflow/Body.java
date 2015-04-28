package io.yaas.workflow;

import io.yaas.workflow.runtime.ActionInstance;

import java.util.function.BiFunction;

public interface Body extends BiFunction<ActionInstance, Arguments, ActionResult> {
}
