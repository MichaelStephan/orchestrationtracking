package io.yaas.workflow.action;

import io.yaas.workflow.Workflow;
import org.junit.Before;
import org.junit.Test;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

/**
 * Created by D032705 on 30.06.2015.
 */
public class SplitActionTest {

    private Workflow workflow;
    private Action splitAction;

    @Before
    public void setUp() {
        workflow = createMock(Workflow.class);
        splitAction = new SplitAction(workflow);
    }

    @Test
    public void testGetName() throws Exception {
        assertEquals("split", splitAction.getName());
    }

    @Test
    public void testGetVersion() throws Exception {
        assertEquals("1.0", splitAction.getVersion());
    }
}