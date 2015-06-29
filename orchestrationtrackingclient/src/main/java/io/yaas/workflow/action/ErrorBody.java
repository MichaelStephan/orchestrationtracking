package io.yaas.workflow.action;

import java.util.function.BiFunction;

public interface ErrorBody extends BiFunction<Throwable, Arguments, Void> {
}
