package io.yaas.workflow.graph;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by D032705 on 30.06.2015.
 */
public class BaseNodeTest {

    @Test
    public void testGetId() throws Exception {
        Node a = new BaseNode("A");
        assertEquals(a.getId(), "A");
    }

    @Test
    public void testGetPredecessors() throws Exception {
        Node a = new BaseNode("A");
        Node b = new BaseNode("B");
        a.addPredecessor(b);
        assertEquals(a.getPredecessors().size(), 1);
    }

    @Test
    public void testGetSuccessors() throws Exception {
        Node a = new BaseNode("A");
        Node b = new BaseNode("B");
        a.addSuccessor(b);
        assertEquals(a.getSuccessors().size(), 1);
    }

    @Test
    public void testAddSuccessor() throws Exception {
        Node a = new BaseNode("A");
        Node b = new BaseNode("B");
        a.addSuccessor(b);
        assertEquals(a.getSuccessors().size(), 1);
        assertEquals(b.getPredecessors().size(), 1);
        assertEquals(b.getSuccessors().size(), 0);
        assertEquals(a.getPredecessors().size(), 0);
    }

    @Test
    public void testRemoveSuccessor() throws Exception {
        Node a = new BaseNode("A");
        Node b = new BaseNode("B");
        a.addSuccessor(b);
        a.removeSuccessor(b);
        assertEquals(a.getSuccessors().size(), 0);
        assertEquals(b.getPredecessors().size(), 0);
        assertEquals(b.getSuccessors().size(), 0);
        assertEquals(a.getPredecessors().size(), 0);
    }

    @Test
    public void testAddPredecessor() throws Exception {
        Node a = new BaseNode("A");
        Node b = new BaseNode("B");
        a.addPredecessor(b);
        assertEquals(a.getSuccessors().size(), 0);
        assertEquals(b.getPredecessors().size(), 0);
        assertEquals(b.getSuccessors().size(), 1);
        assertEquals(a.getPredecessors().size(), 1);
    }

    @Test
    public void testRemovePredecessor() throws Exception {
        Node a = new BaseNode("A");
        Node b = new BaseNode("B");
        a.addPredecessor(b);
        a.removePredecessor(b);
        assertEquals(a.getSuccessors().size(), 0);
        assertEquals(b.getPredecessors().size(), 0);
        assertEquals(b.getSuccessors().size(), 0);
        assertEquals(a.getPredecessors().size(), 0);
    }

    @Test
    public void test_addSuccessor() throws Exception {
        Node a = new BaseNode("A");
        Node b = new BaseNode("B");
        a._addSuccessor(b);
        assertEquals(a.getSuccessors().size(), 1);
        assertEquals(b.getPredecessors().size(), 0);
        assertEquals(b.getSuccessors().size(), 0);
        assertEquals(a.getPredecessors().size(), 0);
    }

    @Test
    public void test_removeSuccessor() throws Exception {
        Node a = new BaseNode("A");
        Node b = new BaseNode("B");
        a._addSuccessor(b);
        a._removeSuccessor(b);
        assertEquals(a.getSuccessors().size(), 0);
        assertEquals(b.getPredecessors().size(), 0);
        assertEquals(b.getSuccessors().size(), 0);
        assertEquals(a.getPredecessors().size(), 0);
    }

    @Test
    public void test_addPredecessor() throws Exception {
        Node a = new BaseNode("A");
        Node b = new BaseNode("B");
        a._addPredecessor(b);
        assertEquals(a.getSuccessors().size(), 0);
        assertEquals(b.getPredecessors().size(), 0);
        assertEquals(b.getSuccessors().size(), 0);
        assertEquals(a.getPredecessors().size(), 1);
    }

    @Test
    public void test_removePredecessor() throws Exception {
        Node a = new BaseNode("A");
        Node b = new BaseNode("B");
        a._addPredecessor(b);
        a._removePredecessor(b);
        assertEquals(a.getSuccessors().size(), 0);
        assertEquals(b.getPredecessors().size(), 0);
        assertEquals(b.getSuccessors().size(), 0);
        assertEquals(a.getPredecessors().size(), 0);
    }

    @Test
    public void testInsertAfter() throws Exception {
        Node a = new BaseNode("A");
        Node b = new BaseNode("B");
        a.insertAfter(b);
        assertEquals(a.getSuccessors().size(), 1);
        assertEquals(b.getPredecessors().size(), 1);
        assertEquals(b.getSuccessors().size(), 0);
        assertEquals(a.getPredecessors().size(), 0);
    }

    @Test
    public void testInsertBefore() throws Exception {
        Node a = new BaseNode("A");
        Node b = new BaseNode("B");
        a.insertBefore(b);
        assertEquals(a.getSuccessors().size(), 0);
        assertEquals(b.getPredecessors().size(), 0);
        assertEquals(b.getSuccessors().size(), 1);
        assertEquals(a.getPredecessors().size(), 1);
    }
}