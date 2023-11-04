package uk.ac.ed.inf;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;
import org.junit.Test;
import uk.ac.ed.inf.Data.MainLngLatHandle;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;


/**
 * Unit test for simple Main.
 */
public class MainTest
{
    public static LngLat[] centralAreaCoordinates;
    public static LngLat[] sampleIntCoordinates;
    @BeforeClass
    public static void setUp(){

        LngLat p1 = new LngLat(0,0);
        LngLat p2 = new LngLat(3,2);
        LngLat p3 = new LngLat(4,-1);
        LngLat p4 = new LngLat(0,-4);
        LngLat p5 = new LngLat(-5,-2);
        LngLat p6 = new LngLat(-2,-1);
        LngLat p7 = new LngLat(-2,2);
        sampleIntCoordinates = new LngLat[]{p1,p2,p3,p4,p5,p6,p7};

        LngLat c1 = new LngLat(-3.192473,  55.946233);
        LngLat c2 = new LngLat(-3.192473,  55.942617);
        LngLat c3 = new LngLat(-3.184319,  55.942617);
        LngLat c4 = new LngLat(-3.184319,  55.946233);
        centralAreaCoordinates = new LngLat[]{c1,c2,c3,c4};

    }
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

    @Test
    public void isInRegion(){
        LngLat position = new LngLat(-3.192473,  55.942617);
        LngLat[] regionCoordinates = {new LngLat(-3.192473,  55.946233),
                new LngLat(-3.192473,  55.942617),
                new LngLat(-3.184319,  55.942617),
                new LngLat(-3.184319,  55.946233)};
        NamedRegion region = new NamedRegion("test", regionCoordinates);
        MainLngLatHandle lngLatHandler = new MainLngLatHandle();
        boolean result = lngLatHandler.isInRegion(position, region);
        assertTrue(result);
    }

    // Tests with diagram
    @Test
    public void isInRegionWithPointAsVertex1(){
        LngLat position = new LngLat(0,-4);
        NamedRegion region = new NamedRegion("test", sampleIntCoordinates);
        MainLngLatHandle lngLatHandler = new MainLngLatHandle();
        boolean result = lngLatHandler.isInRegion(position, region);
        assertTrue(result);
    }

    @Test
    public void isInRegionWithPointAsVertex2(){
        LngLat position = new LngLat(-2,-1);
        NamedRegion region = new NamedRegion("test", sampleIntCoordinates);
        MainLngLatHandle lngLatHandler = new MainLngLatHandle();
        boolean result = lngLatHandler.isInRegion(position, region);
        assertTrue(result);
    }

    @Test
    public void isInRegionWithPointOnEdge1(){
        LngLat position = new LngLat(0,-2);
        NamedRegion region = new NamedRegion("test", sampleIntCoordinates);
        MainLngLatHandle lngLatHandler = new MainLngLatHandle();
        boolean result = lngLatHandler.isInRegion(position, region);
        assertTrue(result);
    }

    @Test
    public void isInRegionWithPointOnEdge2(){
        LngLat position = new LngLat(-1,1);
        NamedRegion region = new NamedRegion("test", sampleIntCoordinates);
        MainLngLatHandle lngLatHandler = new MainLngLatHandle();
        boolean result = lngLatHandler.isInRegion(position, region);
        assertTrue(result);
    }

    @Test
    public void isInRegionWithPointInside1(){
        LngLat position = new LngLat(-2,0);
        NamedRegion region = new NamedRegion("test", sampleIntCoordinates);
        MainLngLatHandle lngLatHandler = new MainLngLatHandle();
        boolean result = lngLatHandler.isInRegion(position, region);
        assertTrue(result);
    }
    @Test
    public void isInRegionWithPointInside2(){
        LngLat position = new LngLat(3,-1);
        NamedRegion region = new NamedRegion("test", sampleIntCoordinates);
        MainLngLatHandle lngLatHandler = new MainLngLatHandle();
        boolean result = lngLatHandler.isInRegion(position, region);
        assertTrue(result);
    }

    @Test
    public void isInRegionWithPointOutside1(){
        LngLat position = new LngLat(2,4);
        NamedRegion region = new NamedRegion("test", sampleIntCoordinates);
        MainLngLatHandle lngLatHandler = new MainLngLatHandle();
        boolean result = lngLatHandler.isInRegion(position, region);
        assertFalse(result);
    }

    @Test
    public void isInRegionWithPointOutside2(){
        LngLat position = new LngLat(-5,-4);
        NamedRegion region = new NamedRegion("test", sampleIntCoordinates);
        MainLngLatHandle lngLatHandler = new MainLngLatHandle();
        boolean result = lngLatHandler.isInRegion(position, region);
        assertFalse(result);
    }

    @Test
    public void centralAreaPointInside2(){
        LngLat position = new LngLat(-3.189110,  55.9440);
        NamedRegion region = new NamedRegion("test", centralAreaCoordinates);
        MainLngLatHandle lngLatHandler = new MainLngLatHandle();
        LngLat nextPosition = lngLatHandler.nextPosition(position, 90);
        boolean result = lngLatHandler.isInRegion(nextPosition, region);
        assertTrue(result);
    }

}

