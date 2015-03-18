package io.yaas.workflow;

import io.yaas.workflow.Workflow.Action;

import java.util.concurrent.Future;

import rx.functions.Func3;

import com.google.common.util.concurrent.SettableFuture;

public interface Body extends Func3<Action, Arguments, SettableFuture<ActionResult>, Future<ActionResult>> {

}
