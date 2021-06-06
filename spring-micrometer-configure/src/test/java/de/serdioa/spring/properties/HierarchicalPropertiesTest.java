package de.serdioa.spring.properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;


public class HierarchicalPropertiesTest {

    private HierarchicalProperties<Integer> props;


    @Before
    public void setUp() {
        Map<String, Integer> propsMap = new HashMap<>();
        propsMap.put("foo", 1);
        propsMap.put("bar", 2);
        propsMap.put("foo.zip", 3);
        propsMap.put("foo.zap", 4);
        propsMap.put("foo.zip.bang", 5);

        this.props = new HierarchicalProperties<>(propsMap);
    }


    @Test
    public void testGetExact() {
        assertEquals(1, (int) this.props.get("foo").get());
        assertEquals(2, (int) this.props.get("bar").get());
        assertEquals(3, (int) this.props.get("foo.zip").get());
        assertEquals(4, (int) this.props.get("foo.zap").get());
        assertEquals(5, (int) this.props.get("foo.zip.bang").get());
    }


    @Test
    public void testGetPrefix() {
        // No exact match, using the prefix "foo.zip".
        assertEquals(3, (int) this.props.get("foo.zip.nomatch").get());

        // No exact match, using the prefix "bar".
        assertEquals(2, (int) this.props.get("bar.nomatch.nomatch.nomatch").get());
    }


    @Test
    public void testGetNotFound() {
        assertTrue(this.props.get("nomatch").isEmpty());
    }


    @Test
    public void testGetFallback() {
        assertEquals(9, (int) this.props.get("nomatch", 9));
    }
}
