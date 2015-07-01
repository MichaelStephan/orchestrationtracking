package io.yaas.workflow.runtime;

import io.yaas.workflow.action.Arguments;
import io.yaas.workflow.runtime.action.instance.WorkflowInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by i303874 on 7/1/15.
 */
public class PerformanceWorkflowEngineResultHandler implements WorkflowEngineResultHandler {

    private static final Logger logger = LoggerFactory.getLogger(PerformanceWorkflowEngineResultHandler.class);


    private final static long INTERVAL = 5000;

    AtomicLong last = new AtomicLong(System.currentTimeMillis());

    AtomicLong count = new AtomicLong();

    @Override
    public void succeeded(WorkflowInstance workflow, Arguments results) {
        update();
    }

    @Override
    public void failed(WorkflowInstance workflow, Throwable cause) {
        update();
    }

    @Override
    public void compensated(WorkflowInstance workflow) {
        update();
    }

    private void update() {
        long now = System.currentTimeMillis();
        long count = this.count.incrementAndGet();
        long diff = now - last.get();
        if (diff > INTERVAL) {
            last.set(System.currentTimeMillis());
            this.count.set(0);
            logger.info(count + " events per " + diff / 1000.0 + " seconds");
        }
    }
}
