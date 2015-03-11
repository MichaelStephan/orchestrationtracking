package io.yaas.workflow;

import io.yaas.workflow.impl.RxJavaWorkflowExecutor;

public class Test {

	public static void main(String[] args) {
		Workflow w = new Workflow("test", "1", new RxJavaWorkflowExecutor());
		w.setErrorHandler((exception) -> { 
			System.out.println("global failure");
		}, (exception) -> { 
			System.out.println("global unknown");
		}).getStartAction().addAction((ignore) -> {
			System.out.println("action 1");
			return "1";
		}).setErrorHandler((exception) -> { 
			System.out.println("local failure");
		}, (exception) -> { 
			System.out.println("local unknown");
		});
	}

}
