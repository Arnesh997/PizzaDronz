package uk.ac.ed.inf;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.interfaces.LngLatHandling;


/**
 * Unit test for simple Main.
 */
public class MainTest
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue()
    {
        assertTrue( true );
    }
    @Test
    public void testInRegion(){
        MainLngLatHandle test = new MainLngLatHandle();
        LngLat position = new LngLat(-3.188396, 55.944425);
        NamedRegion region = new NamedRegion("test", new LngLat[] {
                new LngLat(-3.192473,55.946233),
                new LngLat(-3.184319,55.946233),
                new LngLat(-3.192473,55.942617),
                new LngLat(-3.184319,55.942617)
        });
        assertTrue(test.isInRegion(position, region));
    }
    @Test
    public void testOutsideInformaticsForum(){
        MainLngLatHandle test = new MainLngLatHandle();
        LngLat position = new LngLat(-3.18745, 55.94524);
        NamedRegion region = new NamedRegion("test", new LngLat[] {
                new LngLat(-3.1876927614212036, 55.94520696732767),
                new LngLat(-3.187555968761444, 55.9449621408666),
                new LngLat(-3.186981976032257, 55.94505676722831),
                new LngLat(-3.1872327625751495, 55.94536993377657),
                new LngLat(-3.1874459981918335, 55.9453361389472),
                new LngLat(-3.1873735785484314, 55.94519344934259)
        });
        assertFalse(test.isInRegion(position, region));
    }
}
