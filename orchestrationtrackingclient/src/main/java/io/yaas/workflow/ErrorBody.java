package io.yaas.workflow;

import java.util.function.BiFunction;

public interface ErrorBody extends BiFunction<Throwable, Arguments, Void> {
}
