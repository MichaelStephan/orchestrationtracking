package io.yaas.workflow.action;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by D032705 on 30.06.2015.
 */
public class EndActionTest {

    private Action endAction;

    @Before
    public void setUp() {
        endAction = new EndAction();
    }

    @Test
    public void testGetId() throws Exception {
        assertEquals("end.1.0", endAction.getId());
    }

    @Test
    public void testGetName() throws Exception {
        assertEquals("end", endAction.getName());
    }

    @Test
    public void testGetVersion() throws Exception {
        assertEquals("1.0", endAction.getVersion());
    }
}