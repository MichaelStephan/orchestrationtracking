package io.yaas.workflow.runtime.action.instance;

import com.google.common.util.concurrent.SettableFuture;
import io.yaas.workflow.Workflow;
import io.yaas.workflow.action.ActionResult;
import io.yaas.workflow.action.Arguments;
import io.yaas.workflow.action.SplitAction;
import io.yaas.workflow.runtime.ActionInstance;
import io.yaas.workflow.runtime.WorkflowInstance;
import org.junit.Before;
import org.junit.Test;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

/**
 * Created by D032705 on 30.06.2015.
 */
public class SplitActionInstanceTest {

    private Workflow workflow;
    private WorkflowInstance workflowInstance;
    private SplitAction splitAction;
    private ActionInstance splitActionInstance;

    @Before
    public void setUp() {
        workflow = createMock(Workflow.class);
        workflowInstance = createMock(WorkflowInstance.class);
        splitAction = new SplitAction(workflow);
        splitActionInstance = new SplitActionInstance("2", splitAction, 2);
    }

    @Test
    public void testGetCompensationActionInstance() throws Exception {
        ActionInstance splitCompenstionActionInstance = splitActionInstance.getCompensationActionInstance();
        assertNotNull(splitCompenstionActionInstance);
        assertEquals(SplitCompensationActionInstance.class, splitCompenstionActionInstance.getClass());
    }

    @Test
    public void testExecute() throws Exception {
        SettableFuture<ActionResult> result = SettableFuture.create();
        splitActionInstance.execute(workflowInstance, Arguments.EMPTY_ARGUMENTS, result);
        assertTrue(result.isDone());
        assertEquals(Arguments.EMPTY_ARGUMENTS, result.get().getResult());
    }

    @Test
    public void testRestore() throws Exception {

    }

    @Test
    public void testGetId() throws Exception {
        assertEquals("2_split_1.0", splitActionInstance.getId());
    }

    @Test
    public void testGetName() throws Exception {
        assertEquals("split", splitActionInstance.getName());
    }

    @Test
    public void testGetVersion() throws Exception {
        assertEquals("1.0", splitActionInstance.getVersion());
    }

    @Test
    public void testStart() throws Exception {

    }

    @Test
    public void testSucceed() throws Exception {

    }

    @Test
    public void testError() throws Exception {

    }

    @Test
    public void testGetAction() throws Exception {
        assertEquals(splitAction, splitActionInstance.getAction());
    }
}