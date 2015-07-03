package io.yaas.workflow.action;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by D032705 on 30.06.2015.
 */
public class NopFunctionTest {

    @Test
    public void testApply() throws Exception {
        assertEquals(ActionResult.EMPTY_RESULT, new NopFunction().apply(Arguments.EMPTY_ARGUMENTS));
    }
}