package io.yaas.workflow.action;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Created by D032705 on 30.06.2015.
 */
public class ArgumentsTest {

    private Arguments arguments;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Before
    public void setUp() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("testKey1", "testValue1");
        map.put("testKey2", "testValue2");
        arguments = new Arguments(map);
    }

    @Test
    public void testClear() throws Exception {
        exception.expect(UnsupportedOperationException.class);
        arguments.clear();
    }

    @Test
    public void testContainsKey() throws Exception {
        assertTrue(arguments.containsKey("testKey1"));
    }

    @Test
    public void testContainsValue() throws Exception {
        assertTrue(arguments.containsValue("testValue1"));
    }

    @Test
    public void testEntrySet() throws Exception {
        Set<Map.Entry<String, Object>> entries = arguments.entrySet();
        assertEquals(2, entries.size());
    }

    @Test
    public void testGet() throws Exception {
        assertEquals("testValue1", arguments.get("testKey1"));
    }

    @Test
    public void testIsEmpty() throws Exception {
        assertFalse(arguments.isEmpty());
    }

    @Test
    public void testKeySet() throws Exception {
        Set<String> keys = arguments.keySet();
        assertEquals(2, keys.size());
    }

    @Test
    public void testPut() throws Exception {
        exception.expect(UnsupportedOperationException.class);
        arguments.put("testKey3", "testValue3");
    }

    @Test
    public void testPutAll() throws Exception {
        Map<String, Object> m = new HashMap<>();
        m.put("testKey3", "testValue3");
        exception.expect(UnsupportedOperationException.class);
        arguments.putAll(m);
    }

    @Test
    public void testRemove() throws Exception {
        exception.expect(UnsupportedOperationException.class);
        arguments.remove("testKey1");
    }

    @Test
    public void testSize() throws Exception {
        assertEquals(2, arguments.size());
    }

    @Test
    public void testValues() throws Exception {
        assertEquals(2, arguments.values().size());
    }

    @Test
    public void testAddError() throws Exception {
        Throwable e = new Exception();
        arguments.addError(e);
        assertEquals(e, arguments.getError());
    }

    @Test
    public void testGetError() throws Exception {
        assertNull(arguments.getError());
    }
}