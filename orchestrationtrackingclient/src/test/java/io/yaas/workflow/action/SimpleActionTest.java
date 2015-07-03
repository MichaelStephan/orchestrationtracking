package io.yaas.workflow.action;

import io.yaas.workflow.Workflow;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by D032705 on 30.06.2015.
 */
public class SimpleActionTest {

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void testGetId() throws Exception {
        Action a = new SimpleAction("A", "1.0");
        assertEquals(a.getId(), "A.1.0");
    }

    @Test
    public void testGetPredecessors() throws Exception {
        Action a = new SimpleAction("A", "1.0");
        Action b = new SimpleAction("B", "1.0");
        a.addPredecessor(b);
        assertEquals(a.getPredecessors().size(), 1);
    }

    @Test
    public void testGetSuccessors() throws Exception {
        Action a = new SimpleAction("A", "1.0");
        Action b = new SimpleAction("B", "1.0");
        a.addSuccessor(b);
        assertEquals(a.getSuccessors().size(), 1);
    }

    @Test
    public void testAddSuccessor() throws Exception {
        Action a = new SimpleAction("A", "1.0");
        Action b = new SimpleAction("B", "1.0");
        a.addSuccessor(b);
        assertEquals(a.getSuccessors().size(), 1);
        assertEquals(b.getPredecessors().size(), 1);
        assertEquals(b.getSuccessors().size(), 0);
        assertEquals(a.getPredecessors().size(), 0);
    }

    @Test
    public void testRemoveSuccessor() throws Exception {
        Action a = new SimpleAction("A", "1.0");
        Action b = new SimpleAction("B", "1.0");
        a.addSuccessor(b);
        a.removeSuccessor(b);
        assertEquals(a.getSuccessors().size(), 0);
        assertEquals(b.getPredecessors().size(), 0);
        assertEquals(b.getSuccessors().size(), 0);
        assertEquals(a.getPredecessors().size(), 0);
    }

    @Test
    public void testAddPredecessor() throws Exception {
        Action a = new SimpleAction("A", "1.0");
        Action b = new SimpleAction("B", "1.0");
        a.addPredecessor(b);
        assertEquals(a.getSuccessors().size(), 0);
        assertEquals(b.getPredecessors().size(), 0);
        assertEquals(b.getSuccessors().size(), 1);
        assertEquals(a.getPredecessors().size(), 1);
    }

    @Test
    public void testRemovePredecessor() throws Exception {
        Action a = new SimpleAction("A", "1.0");
        Action b = new SimpleAction("B", "1.0");
        a.addPredecessor(b);
        a.removePredecessor(b);
        assertEquals(a.getSuccessors().size(), 0);
        assertEquals(b.getPredecessors().size(), 0);
        assertEquals(b.getSuccessors().size(), 0);
        assertEquals(a.getPredecessors().size(), 0);
    }

    @Test
    public void test_addSuccessor() throws Exception {
        Action a = new SimpleAction("A", "1.0");
        Action b = new SimpleAction("B", "1.0");
        a._addSuccessor(b);
        assertEquals(a.getSuccessors().size(), 1);
        assertEquals(b.getPredecessors().size(), 0);
        assertEquals(b.getSuccessors().size(), 0);
        assertEquals(a.getPredecessors().size(), 0);
    }

    @Test
    public void test_removeSuccessor() throws Exception {
        Action a = new SimpleAction("A", "1.0");
        Action b = new SimpleAction("B", "1.0");
        a._addSuccessor(b);
        a._removeSuccessor(b);
        assertEquals(a.getSuccessors().size(), 0);
        assertEquals(b.getPredecessors().size(), 0);
        assertEquals(b.getSuccessors().size(), 0);
        assertEquals(a.getPredecessors().size(), 0);
    }

    @Test
    public void test_addPredecessor() throws Exception {
        Action a = new SimpleAction("A", "1.0");
        Action b = new SimpleAction("B", "1.0");
        a._addPredecessor(b);
        assertEquals(a.getSuccessors().size(), 0);
        assertEquals(b.getPredecessors().size(), 0);
        assertEquals(b.getSuccessors().size(), 0);
        assertEquals(a.getPredecessors().size(), 1);
    }

    @Test
    public void test_removePredecessor() throws Exception {
        Action a = new SimpleAction("A", "1.0");
        Action b = new SimpleAction("B", "1.0");
        a._addPredecessor(b);
        a._removePredecessor(b);
        assertEquals(a.getSuccessors().size(), 0);
        assertEquals(b.getPredecessors().size(), 0);
        assertEquals(b.getSuccessors().size(), 0);
        assertEquals(a.getPredecessors().size(), 0);
    }

    @Test
    public void testInsertAfter() throws Exception {
        Action a = new SimpleAction("A", "1.0");
        Action b = new SimpleAction("B", "1.0");
        a.insertAfter(b);
        assertEquals(a.getSuccessors().size(), 1);
        assertEquals(b.getPredecessors().size(), 1);
        assertEquals(b.getSuccessors().size(), 0);
        assertEquals(a.getPredecessors().size(), 0);
    }

    @Test
    public void testInsertBefore() throws Exception {
        Action a = new SimpleAction("A", "1.0");
        Action b = new SimpleAction("B", "1.0");
        a.insertBefore(b);
        assertEquals(a.getSuccessors().size(), 0);
        assertEquals(b.getPredecessors().size(), 0);
        assertEquals(b.getSuccessors().size(), 1);
        assertEquals(a.getPredecessors().size(), 1);
    }

    @Test
    public void testSetActionFunction() throws Exception {
        Action a = new SimpleAction("A", "1.0");
//        assertNotNull(a.getActionFunction());
        a.setActionFunction((arguments) -> {
            return ActionResult.EMPTY_RESULT;
        });
        assertNotNull(a.getActionFunction());
    }

    @Test
    public void testSetCompensationFunction() throws Exception {
        Action a = new SimpleAction("A", "1.0");
        assertNotNull(a.getCompensationFunction());
        a.setCompensationFunction((arguments) -> {
            return ActionResult.EMPTY_RESULT;
        });
        assertNotNull(a.getCompensationFunction());
    }

    @Test
    public void testGetCompensationFunction() throws Exception {
        Action a = new SimpleAction("A", "1.0");
        assertNotNull(a.getCompensationFunction());
        a.setCompensationFunction((arguments) -> {
            return ActionResult.EMPTY_RESULT;
        });
        assertNotNull(a.getCompensationFunction());
    }

    @Test
    public void testAddAction() throws Exception {
        Action a = new SimpleAction("A", "1.0");
        Action b = new SimpleAction("B", "1.0");
        assertSame(b, a.addAction(b));
        assertEquals(a.getSuccessors().size(), 1);
        assertEquals(b.getPredecessors().size(), 1);
        assertEquals(b.getSuccessors().size(), 0);
        assertEquals(a.getPredecessors().size(), 0);
    }

    @Test
    public void testGetWorkflow() throws Exception {
        Workflow w = new Workflow("W", 1);
        Action a = new SimpleAction("A", "1.0", w);
        assertEquals(a.getWorkflow(), w);
    }

    @Test
    public void testSetWorkflow() throws Exception {
        Workflow w = new Workflow("W", 1);
        Action a = new SimpleAction("A", "1.0");
        assertNull(a.getWorkflow());
        a.setWorkflow(w);
        assertEquals(a.getWorkflow(), w);
    }

    @Test
    public void testGetActionFunction() throws Exception {
        Action a = new SimpleAction("A", "1.0", (arguments) -> {
            return ActionResult.EMPTY_RESULT;
        });
        assertNotNull(a.getActionFunction());
    }

    @Test
    public void testGetName() throws Exception {
        Action a = new SimpleAction("A", "1.0");
        assertEquals(a.getName(), "A");
   }

    @Test
    public void testGetVersion() throws Exception {
        Action a = new SimpleAction("A", "1.0");
        assertEquals(a.getVersion(), "1.0");
    }
}