package io.yaas.workflow.runtime.action.instance;

import static org.easymock.EasyMock.*;

import com.google.common.util.concurrent.SettableFuture;
import io.yaas.workflow.Workflow;
import io.yaas.workflow.action.*;
import io.yaas.workflow.runtime.ActionInstance;
import io.yaas.workflow.runtime.tracker.client.WorkflowTrackingClient;
import io.yaas.workflow.runtime.tracker.model.ActionBean;
import io.yaas.workflow.runtime.tracker.model.ResultBean;
import org.easymock.EasyMock;
import org.easymock.Mock;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by D032705 on 30.06.2015.
 */
public class SimpleActionInstanceTest {

    private Action actionA;
    private Action actionB;
    private ActionInstance actionInstanceA;
    private ActionInstance actionInstanceB;
//    private Workflow workflow;
    private WorkflowInstance workflowInstance;

//    @Mock
    private WorkflowTrackingClient trackingClient;


    @Before
    public void setUp() throws Exception {
        actionA = new SimpleAction("A", "1.0");
        actionB = new SimpleAction("B", "1.0");
//        workflow = new Workflow("W", 1);
        //workflow.getStartAction().addAction(actionA).addAction(actionB);
        actionInstanceA = new SimpleActionInstance("3", actionA);
        actionInstanceB = new SimpleActionInstance("4", actionB);
        trackingClient = createMock(WorkflowTrackingClient.class);
        //ActionBean actionBean = createMock(ActionBean.class);
        //ResultBean resultBean = createMock(ResultBean.class);
        expect(trackingClient.createAction(EasyMock.isA(ActionBean.class))).andStubReturn(EasyMock.isA(ActionBean.class));
        expect(trackingClient.updateAction(EasyMock.isA(ActionBean.class))).andStubReturn(EasyMock.isA(ActionBean.class));
        expect(trackingClient.getLastActionData("", "")).andStubReturn(EasyMock.isA(ResultBean.class));
        workflowInstance = createMock(WorkflowInstance.class);
        expect(workflowInstance.getId()).andStubReturn("1");
        expect(workflowInstance.getTrackingClient()).andStubReturn(trackingClient);

        //(workflow, trackingClient, new StartActionInstance("1", new StartAction()), new EndActionInstance("5", new EndAction()));
        //replayAll();
        //replay(actionBean);
        replay(trackingClient);
        replay(workflowInstance);
    }

    @Test
    public void testGetName() throws Exception {
        assertEquals("A", actionInstanceA.getName());
    }

    @Test
    public void testGetVersion() throws Exception {
        assertEquals("1.0", actionInstanceA.getVersion());
    }

    @Test
    public void testGetCompensationActionInstanceInstance() throws Exception {
        assertEquals(SimpleCompensationActionInstance.class, actionInstanceA.getCompensationActionInstance().getClass());
    }

    @Test
    public void testStart() throws Exception {
        actionInstanceA.start(workflowInstance);
    }

    @Test
    public void testSucceed() throws Exception {
        actionInstanceA.succeed(workflowInstance, new ActionResult(Arguments.EMPTY_ARGUMENTS));
    }

    @Test
    public void testError() throws Exception {
        actionInstanceA.error(workflowInstance, new Exception());
    }

    @Test
    public void testExecute() throws Exception {
        actionInstanceA.execute(workflowInstance, Arguments.EMPTY_ARGUMENTS, SettableFuture.<ActionResult>create());
    }

    @Test
    public void testRestore() throws Exception {
        actionInstanceA.restore(workflowInstance);
    }

    @Test
    public void testGetId() throws Exception {
        assertEquals("3_A_1.0", actionInstanceA.getId());
    }

    @Test
    public void testGetPredecessors() throws Exception {
        actionInstanceA.addPredecessor(actionInstanceB);
        assertEquals(1, actionInstanceA.getPredecessors().size());
    }

    @Test
    public void testGetSuccessors() throws Exception {
        actionInstanceA.addSuccessor(actionInstanceB);
        assertEquals(1, actionInstanceA.getSuccessors().size());
    }

    @Test
    public void testAddSuccessor() throws Exception {
        actionInstanceA.addSuccessor(actionInstanceB);
        assertEquals(1, actionInstanceA.getSuccessors().size());
        assertEquals(1, actionInstanceB.getPredecessors().size());
        assertEquals(0, actionInstanceB.getSuccessors().size());
        assertEquals(0, actionInstanceA.getPredecessors().size());
    }

    @Test
    public void testRemoveSuccessor() throws Exception {
        actionInstanceA.addSuccessor(actionInstanceB);
        actionInstanceA.removeSuccessor(actionInstanceB);
        assertEquals(0, actionInstanceA.getSuccessors().size());
        assertEquals(0, actionInstanceB.getPredecessors().size());
        assertEquals(0, actionInstanceB.getSuccessors().size());
        assertEquals(0, actionInstanceA.getPredecessors().size());
    }

    @Test
    public void testAddPredecessor() throws Exception {
        actionInstanceA.addPredecessor(actionInstanceB);
        assertEquals(0, actionInstanceA.getSuccessors().size());
        assertEquals(0, actionInstanceB.getPredecessors().size());
        assertEquals(1, actionInstanceB.getSuccessors().size());
        assertEquals(1, actionInstanceA.getPredecessors().size());
    }

    @Test
    public void testRemovePredecessor() throws Exception {
        actionInstanceA.addPredecessor(actionInstanceB);
        actionInstanceA.removePredecessor(actionInstanceB);
        assertEquals(0, actionInstanceA.getSuccessors().size());
        assertEquals(0, actionInstanceB.getPredecessors().size());
        assertEquals(0, actionInstanceB.getSuccessors().size());
        assertEquals(0, actionInstanceA.getPredecessors().size());
    }

    @Test
    public void test_addSuccessor() throws Exception {
        actionInstanceA._addSuccessor(actionInstanceB);
        assertEquals(actionInstanceA.getSuccessors().size(), 1);
        assertEquals(actionInstanceB.getPredecessors().size(), 0);
        assertEquals(actionInstanceB.getSuccessors().size(), 0);
        assertEquals(actionInstanceA.getPredecessors().size(), 0);
    }

    @Test
    public void test_removeSuccessor() throws Exception {
        actionInstanceA._addSuccessor(actionInstanceB);
        actionInstanceA._removeSuccessor(actionInstanceB);
        assertEquals(actionInstanceA.getSuccessors().size(), 0);
        assertEquals(actionInstanceB.getPredecessors().size(), 0);
        assertEquals(actionInstanceB.getSuccessors().size(), 0);
        assertEquals(actionInstanceA.getPredecessors().size(), 0);
    }

    @Test
    public void test_addPredecessor() throws Exception {
        actionInstanceA._addPredecessor(actionInstanceB);
        assertEquals(actionInstanceA.getSuccessors().size(), 0);
        assertEquals(actionInstanceB.getPredecessors().size(), 0);
        assertEquals(actionInstanceB.getSuccessors().size(), 0);
        assertEquals(actionInstanceA.getPredecessors().size(), 1);
    }

    @Test
    public void test_removePredecessor() throws Exception {
        actionInstanceA._addPredecessor(actionInstanceB);
        actionInstanceA._removePredecessor(actionInstanceB);
        assertEquals(actionInstanceA.getSuccessors().size(), 0);
        assertEquals(actionInstanceB.getPredecessors().size(), 0);
        assertEquals(actionInstanceB.getSuccessors().size(), 0);
        assertEquals(actionInstanceA.getPredecessors().size(), 0);
    }

    @Test
    public void testInsertAfter() throws Exception {
        actionInstanceA.insertAfter(actionInstanceB);
        assertEquals(actionInstanceA.getSuccessors().size(), 1);
        assertEquals(actionInstanceB.getPredecessors().size(), 1);
        assertEquals(actionInstanceB.getSuccessors().size(), 0);
        assertEquals(actionInstanceA.getPredecessors().size(), 0);
    }

    @Test
    public void testInsertBefore() throws Exception {
        actionInstanceA.insertBefore(actionInstanceB);
        assertEquals(actionInstanceA.getSuccessors().size(), 0);
        assertEquals(actionInstanceB.getPredecessors().size(), 0);
        assertEquals(actionInstanceB.getSuccessors().size(), 1);
        assertEquals(actionInstanceA.getPredecessors().size(), 1);
    }

}