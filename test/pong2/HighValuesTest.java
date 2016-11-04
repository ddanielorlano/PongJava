package pong2;

import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

public class HighValuesTest {

    HighValues vals;

    @Before
    public void run() {
        vals = new HighValues(3);

        vals.add(5);
        vals.add(8);
        vals.add(10);
        vals.add(12);
        vals.add(15);
        vals.add(30);
    }

    @Test
    public void testAdd() {
        assertEquals(vals.get(5), 30);
        assertEquals(vals.get(4), 15);
        assertEquals(vals.get(3), 12);
        assertEquals(vals.get(2), 10);
        assertEquals(vals.get(1), 8);
        assertEquals(vals.get(0), 5);

    }

    /**
     * Assert that values stored in the list are ascending
     */
    @Test
    public void testSort() {
        
        vals.sortDescending();

        assertEquals(vals.get(0), 30);
        assertEquals(vals.get(1), 15);
        assertEquals(vals.get(2), 12);
        assertEquals(vals.get(3), 10);
        assertEquals(vals.get(4), 8);
        assertEquals(vals.get(5), 5);
    }
}
