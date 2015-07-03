package io.yaas.workflow.runtime.action.instance;

import com.google.common.util.concurrent.SettableFuture;
import io.yaas.workflow.action.*;
import io.yaas.workflow.runtime.ActionInstance;
import io.yaas.workflow.runtime.WorkflowEngineResultHandler;
import io.yaas.workflow.runtime.tracker.client.WorkflowTrackingClient;
import io.yaas.workflow.runtime.tracker.model.ActionBean;
import org.junit.Before;
import org.junit.Test;

import static org.easymock.EasyMock.*;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.*;

/**
 * Created by D032705 on 30.06.2015.
 */
public class EndActionInstanceTest {

    private Action endAction;
    private ActionInstance endActionInstance;
    private WorkflowInstance workflowInstance;
    private WorkflowTrackingClient trackingClient;


    @Before
    public void setUp() throws Exception {
        endAction = new EndAction();
        endActionInstance = new EndActionInstance("2", endAction);
        trackingClient = createMock(WorkflowTrackingClient.class);
        workflowInstance = createMock(WorkflowInstance.class);
    }

    @Test
    public void testStart() throws Exception {
        expect(trackingClient.createAction(isA(ActionBean.class))).andStubReturn(createMock(ActionBean.class));

        expect(workflowInstance.getId()).andStubReturn("1");
        expect(workflowInstance.getTrackingClient()).andStubReturn(trackingClient);

        replay(trackingClient);
        replay(workflowInstance);

        endActionInstance.start(workflowInstance);

        verify(trackingClient);
        verify(workflowInstance);
    }

    @Test
    public void testExecute() throws Exception {
        SettableFuture<ActionResult> result = SettableFuture.create();
        endActionInstance.execute(workflowInstance, Arguments.EMPTY_ARGUMENTS, result);
        assertTrue(result.isDone());
        assertEquals(Arguments.EMPTY_ARGUMENTS, result.get().getResult());
    }

    @Test
    public void testRestore() throws Exception {
        assertEquals(ActionResult.EMPTY_RESULT, endActionInstance.restore(workflowInstance));
    }

    @Test
    public void testGetId() throws Exception {
        assertEquals("2_end_1.0", endActionInstance.getId());
    }

    @Test
    public void testGetName() throws Exception {
        assertEquals("end", endActionInstance.getName());
    }

    @Test
    public void testGetVersion() throws Exception {
        assertEquals("1.0", endActionInstance.getVersion());
    }

    @Test
    public void testGetCompensationActionInstance() throws Exception {
        assertNotNull(endActionInstance.getCompensationActionInstance());
        assertEquals(SimpleCompensationActionInstance.class, endActionInstance.getCompensationActionInstance().getClass());
    }

    @Test
    public void testSucceed() throws Exception {
        expect(trackingClient.updateAction(isA(ActionBean.class))).andStubReturn(createMock(ActionBean.class));

        expect(workflowInstance.getId()).andStubReturn("1");
        expect(workflowInstance.getTrackingClient()).andStubReturn(trackingClient);

        workflowInstance.succeed();
        expectLastCall();

        expect(workflowInstance.getResultHandler()).andStubReturn(createMock(WorkflowEngineResultHandler.class));

        replay(trackingClient);
        replay(workflowInstance);

        endActionInstance.succeed(workflowInstance, ActionResult.EMPTY_RESULT);

        verify(trackingClient);
        verify(workflowInstance);
    }

    @Test
    public void testError() throws Exception {

    }

    @Test
    public void testGetAction() throws Exception {
        assertEquals(endAction, endActionInstance.getAction());
    }

}