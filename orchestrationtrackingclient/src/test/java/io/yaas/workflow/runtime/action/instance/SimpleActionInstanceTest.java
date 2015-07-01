package io.yaas.workflow.runtime.action.instance;

import io.yaas.workflow.action.Action;
import io.yaas.workflow.action.SimpleAction;
import io.yaas.workflow.runtime.ActionInstance;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by D032705 on 30.06.2015.
 */
public class SimpleActionInstanceTest {

    private Action actionA;
    private Action actionB;

    @Before
    public void setUp() throws Exception {
        actionA = new SimpleAction("A", "1.0");
        actionB = new SimpleAction("B", "1.0");
    }

    @Test
    public void testGetName() throws Exception {

    }

    @Test
    public void testGetVersion() throws Exception {

    }

    @Test
    public void testCreateCompensationActionInstanceInstance() throws Exception {

    }

    @Test
    public void testGetCompensationActionInstanceInstance() throws Exception {

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
    public void testExecute() throws Exception {

    }

    @Test
    public void testGetActionInstance() throws Exception {

    }

    @Test
    public void testRestore() throws Exception {

    }

    @Test
    public void testGetId() throws Exception {
        ActionInstance a = new SimpleActionInstance("1", actionA);
        assertEquals("1_A_1.0", a.getId());
    }

    @Test
    public void testGetPredecessors() throws Exception {
        ActionInstance a = new SimpleActionInstance("1", actionA);
        ActionInstance b = new SimpleActionInstance("2", actionB);
        a.addPredecessor(b);
        assertEquals(1, a.getPredecessors().size());
    }

    @Test
    public void testGetSuccessors() throws Exception {
        ActionInstance a = new SimpleActionInstance("1", actionA);
        ActionInstance b = new SimpleActionInstance("2", actionB);
        a.addSuccessor(b);
        assertEquals(1, a.getSuccessors().size());
    }

    @Test
    public void testAddSuccessor() throws Exception {
        ActionInstance a = new SimpleActionInstance("1", actionA);
        ActionInstance b = new SimpleActionInstance("2", actionB);
        a.addSuccessor(b);
        assertEquals(1, a.getSuccessors().size());
        assertEquals(1, b.getPredecessors().size());
        assertEquals(0, b.getSuccessors().size());
        assertEquals(0, a.getPredecessors().size());
    }

    @Test
    public void testRemoveSuccessor() throws Exception {
        ActionInstance a = new SimpleActionInstance("1", actionA);
        ActionInstance b = new SimpleActionInstance("2", actionB);
        a.addSuccessor(b);
        a.removeSuccessor(b);
        assertEquals(0, a.getSuccessors().size());
        assertEquals(0, b.getPredecessors().size());
        assertEquals(0, b.getSuccessors().size());
        assertEquals(0, a.getPredecessors().size());
    }

    @Test
    public void testAddPredecessor() throws Exception {
        ActionInstance a = new SimpleActionInstance("1", actionA);
        ActionInstance b = new SimpleActionInstance("2", actionB);
        a.addPredecessor(b);
        assertEquals(0, a.getSuccessors().size());
        assertEquals(0, b.getPredecessors().size());
        assertEquals(1, b.getSuccessors().size());
        assertEquals(1, a.getPredecessors().size());
    }

    @Test
    public void testRemovePredecessor() throws Exception {
        ActionInstance a = new SimpleActionInstance("1", actionA);
        ActionInstance b = new SimpleActionInstance("2", actionB);
        a.addPredecessor(b);
        a.removePredecessor(b);
        assertEquals(0, a.getSuccessors().size());
        assertEquals(0, b.getPredecessors().size());
        assertEquals(0, b.getSuccessors().size());
        assertEquals(0, a.getPredecessors().size());
    }

    @Test
    public void test_addSuccessor() throws Exception {
        ActionInstance a = new SimpleActionInstance("1", actionA);
        ActionInstance b = new SimpleActionInstance("2", actionB);
        a._addSuccessor(b);
        assertEquals(a.getSuccessors().size(), 1);
        assertEquals(b.getPredecessors().size(), 0);
        assertEquals(b.getSuccessors().size(), 0);
        assertEquals(a.getPredecessors().size(), 0);
    }

    @Test
    public void test_removeSuccessor() throws Exception {
        ActionInstance a = new SimpleActionInstance("1", actionA);
        ActionInstance b = new SimpleActionInstance("2", actionB);
        a._addSuccessor(b);
        a._removeSuccessor(b);
        assertEquals(a.getSuccessors().size(), 0);
        assertEquals(b.getPredecessors().size(), 0);
        assertEquals(b.getSuccessors().size(), 0);
        assertEquals(a.getPredecessors().size(), 0);
    }

    @Test
    public void test_addPredecessor() throws Exception {
        ActionInstance a = new SimpleActionInstance("1", actionA);
        ActionInstance b = new SimpleActionInstance("2", actionB);
        a._addPredecessor(b);
        assertEquals(a.getSuccessors().size(), 0);
        assertEquals(b.getPredecessors().size(), 0);
        assertEquals(b.getSuccessors().size(), 0);
        assertEquals(a.getPredecessors().size(), 1);
    }

    @Test
    public void test_removePredecessor() throws Exception {
        ActionInstance a = new SimpleActionInstance("1", actionA);
        ActionInstance b = new SimpleActionInstance("2", actionB);
        a._addPredecessor(b);
        a._removePredecessor(b);
        assertEquals(a.getSuccessors().size(), 0);
        assertEquals(b.getPredecessors().size(), 0);
        assertEquals(b.getSuccessors().size(), 0);
        assertEquals(a.getPredecessors().size(), 0);
    }

    @Test
    public void testInsertAfter() throws Exception {
        ActionInstance a = new SimpleActionInstance("1", actionA);
        ActionInstance b = new SimpleActionInstance("2", actionB);
        a.insertAfter(b);
        assertEquals(a.getSuccessors().size(), 1);
        assertEquals(b.getPredecessors().size(), 1);
        assertEquals(b.getSuccessors().size(), 0);
        assertEquals(a.getPredecessors().size(), 0);
    }

    @Test
    public void testInsertBefore() throws Exception {
        ActionInstance a = new SimpleActionInstance("1", actionA);
        ActionInstance b = new SimpleActionInstance("2", actionB);
        a.insertBefore(b);
        assertEquals(a.getSuccessors().size(), 0);
        assertEquals(b.getPredecessors().size(), 0);
        assertEquals(b.getSuccessors().size(), 1);
        assertEquals(a.getPredecessors().size(), 1);
    }

}