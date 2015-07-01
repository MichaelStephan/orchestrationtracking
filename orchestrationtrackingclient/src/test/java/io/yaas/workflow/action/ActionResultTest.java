package io.yaas.workflow.action;

import junit.framework.TestCase;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

/**
 * Created by D032705 on 30.06.2015.
 */
public class ActionResultTest extends TestCase {

    @Test
    public void testGetResult() throws Exception {
        ActionResult result = new ActionResult(Arguments.EMPTY_ARGUMENTS);
        assertEquals(Arguments.EMPTY_ARGUMENTS, result.getResult());
    }
}