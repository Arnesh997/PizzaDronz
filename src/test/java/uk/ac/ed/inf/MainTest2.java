package uk.ac.ed.inf;import static org.junit.Assert.assertFalse;import static org.junit.Assert.assertTrue;import static  org.junit.Assert.assertEquals;import org.junit.Test;import uk.ac.ed.inf.Data.MainLngLatHandle;import uk.ac.ed.inf.MainOrderValidation;import uk.ac.ed.inf.ilp.data.LngLat;import uk.ac.ed.inf.ilp.data.NamedRegion;import uk.ac.ed.inf.ilp.data.Restaurant;import uk.ac.ed.inf.ilp.interfaces.LngLatHandling;import uk.ac.ed.inf.ilp.interfaces.OrderValidation;import uk.ac.ed.inf.ilp.data.*;import uk.ac.ed.inf.ilp.constant.*;import java.time.DayOfWeek;import java.time.LocalDate;public class MainTest2 {    @Test    public void shouldAnswerWithTrue()    {        assertTrue( true );    }    // Test instance for LngLatHandling    LngLatHandling lngLatHandling = new MainLngLatHandle();    // Test instance for OrderValidation    OrderValidation orderValidation = new MainOrderValidation();    Restaurant[] restaurants = new Restaurant[] {            new Restaurant(                    "Civerinos Slice",                    new LngLat(-3.1912869215011597, 55.945535152517735),                    new DayOfWeek[] {DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY},                    new Pizza[] {                            new Pizza("Margarita", 1000),                            new Pizza("Calzone", 1400)                    }            ),            new Restaurant(                    "Sora Lella Vegan Restaurant",                    new LngLat(-3.202541470527649, 55.943284737579376),                    new DayOfWeek[] {DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY},                    new Pizza[] {                            new Pizza("Meat Lover", 1400),                            new Pizza("Vegan Delight", 1100)                    }            ),            new Restaurant(                    "Domino's Pizza - Edinburgh - Southside",                    new LngLat(-3.1838572025299072, 55.94449876875712),                    new DayOfWeek[] {DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY},                    new Pizza[] {                            new Pizza("Super Cheese", 1400),                            new Pizza("All Shrooms", 900)                    }            ),            new Restaurant(                    "Sodeberg Pavillion",                    new LngLat(-3.1940174102783203, 55.94390696616939),                    new DayOfWeek[] {DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.SATURDAY, DayOfWeek.SUNDAY},                    new Pizza[] {                            new Pizza("Proper Pizza", 1400),                            new Pizza("Pineapple & Ham & Cheese", 900)                    }            )    };    // ========== Tests for LngLatHandling ==========    // Test valid distance calculation    @Test    public void testValidDistanceCalculation() {        double distance = lngLatHandling.distanceTo(new LngLat(0,0), new LngLat(3,4));        assertEquals( 5,distance,0);    }    // Test valid closeness check    @Test    public void testValidClosenessCheck() {        boolean close = lngLatHandling.isCloseTo(new LngLat(0,0), new LngLat(0,0.0001));        assertTrue(close);    }    // Test invalid closeness check    @Test    public void testInvalidClosenessCheck() {        boolean close = lngLatHandling.isCloseTo(new LngLat(0,0), new LngLat(0,0.1));        assertFalse(close);    }    @Test    public void testValidRegionCheck() {        NamedRegion region = new NamedRegion("testRegion", new LngLat[]{new LngLat(0,0), new LngLat(0,1), new LngLat(1,1), new LngLat(1,0)});        assertTrue(lngLatHandling.isInRegion(new LngLat(0.5, 0.5), region));    }    // Test invalid region check    @Test    public void testInvalidRegionCheck() {        NamedRegion region = new NamedRegion("testRegion", new LngLat[]{new LngLat(0,0), new LngLat(0,1), new LngLat(1,1), new LngLat(1,0)});        assertFalse(lngLatHandling.isInRegion(new LngLat(1.5, 1.5), region));    }    @Test    public void testInRegion(){        LngLatHandling test = new MainLngLatHandle();        LngLat position = new LngLat(-3.188396, 55.944425);        NamedRegion region = new NamedRegion("test", new LngLat[] {                new LngLat(-3.192473,55.946233),                new LngLat(-3.184319,55.946233),                new LngLat(-3.192473,55.942617),                new LngLat(-3.184319,55.942617)        });        assertTrue(test.isInRegion(position, region));    }    @Test    public void testValidNextPosition() {        LngLat newPosition = lngLatHandling.nextPosition(new LngLat(0, 0), 0);        assertEquals( 0.00015, newPosition.lng(),0);        assertEquals(0, newPosition.lat(),0);    }    // Test invalid angle for next position    @Test    public void testInvalidNextPosition() {        LngLat newPosition = lngLatHandling.nextPosition(new LngLat(0, 0), 10);        assertEquals(0, newPosition.lng(),0);        assertEquals(0, newPosition.lat(),0);    }    @Test    public void testInvalidCardNumber() {        Order order = new Order("ORD123", LocalDate.now(), OrderStatus.VALID_BUT_NOT_DELIVERED, OrderValidationCode.UNDEFINED, 1100, new Pizza[]{new Pizza("Margarita", 1000)}, new CreditCardInformation("123456789012", "12/25", "123"));        order.setCreditCardInformation(new CreditCardInformation("123456789012", "12/25", "123"));        orderValidation.validateOrder(order, restaurants);        assertEquals(OrderValidationCode.CARD_NUMBER_INVALID, order.getOrderValidationCode());    }    @Test    public void testInvalidCardExpiry() {        Order order = new Order("ORD123", LocalDate.now(), OrderStatus.VALID_BUT_NOT_DELIVERED, OrderValidationCode.UNDEFINED, 1100, new Pizza[]{new Pizza("Margarita", 1000)}, new CreditCardInformation("123456789012", "12/25", "123"));        order.setCreditCardInformation(new CreditCardInformation("4001919257537193", "12/15", "123"));        orderValidation.validateOrder(order, restaurants);        assertEquals(OrderValidationCode.EXPIRY_DATE_INVALID, order.getOrderValidationCode());    }    @Test    public void testInvalidCvvWrongLength() {        Order order = new Order("ORD123", LocalDate.now(), OrderStatus.VALID_BUT_NOT_DELIVERED, OrderValidationCode.UNDEFINED, 1100, new Pizza[]{new Pizza("Margarita", 1000)}, new CreditCardInformation("123456789012", "12/25", "123"));        order.setCreditCardInformation(new CreditCardInformation("4001919257537193", "12/25", "1234"));        orderValidation.validateOrder(order, restaurants);        assertEquals(OrderValidationCode.CVV_INVALID, order.getOrderValidationCode());    }    @Test    public void testInvalidCvvNotDigits() {        Order order = new Order("ORD123", LocalDate.now(), OrderStatus.VALID_BUT_NOT_DELIVERED, OrderValidationCode.UNDEFINED, 1100, new Pizza[]{new Pizza("Margarita", 1000)}, new CreditCardInformation("123456789012", "12/25", "123"));        order.setCreditCardInformation(new CreditCardInformation("4001919257537193", "12/25", "AB1"));        orderValidation.validateOrder(order, restaurants);        assertEquals(OrderValidationCode.CVV_INVALID, order.getOrderValidationCode());    }    @Test    public void testInvalidTotal() {        Order order = new Order("ORD123", LocalDate.now(), OrderStatus.VALID_BUT_NOT_DELIVERED, OrderValidationCode.UNDEFINED, 1500, new Pizza[]{new Pizza("Margarita", 1000)}, new CreditCardInformation("123456789012", "12/25", "123"));        order.setCreditCardInformation(new CreditCardInformation("4001919257537193", "12/25", "123"));        orderValidation.validateOrder(order, restaurants);        assertEquals(OrderValidationCode.TOTAL_INCORRECT, order.getOrderValidationCode());    }    @Test    public void testOrderLimitExceeded() {        Order order = new Order(                "ORD123",                LocalDate.now(),                OrderStatus.VALID_BUT_NOT_DELIVERED,                OrderValidationCode.UNDEFINED,                5100,                new Pizza[]{new Pizza("Margarita", 1000), new Pizza("Margarita", 1000),new Pizza("Margarita", 1000),new Pizza("Margarita", 1000), new Pizza("Margarita", 1000)},                new CreditCardInformation("123456789012", "12/25", "123"));        order.setCreditCardInformation(new CreditCardInformation("4001919257537193", "12/25", "123"));        orderValidation.validateOrder(order, restaurants);        assertEquals(OrderValidationCode.MAX_PIZZA_COUNT_EXCEEDED, order.getOrderValidationCode());    }    @Test    public void testPizzaFromMultipleRestaurants() {        Order order = new Order(                "ORD123",                LocalDate.now(),                OrderStatus.VALID_BUT_NOT_DELIVERED,                OrderValidationCode.UNDEFINED,                2500,                new Pizza[]{new Pizza("Margarita", 1000), new Pizza("Meat Lover", 1400)},                new CreditCardInformation("123456789012", "12/25", "123"));        order.setCreditCardInformation(new CreditCardInformation("4001919257537193", "12/25", "123"));        orderValidation.validateOrder(order, restaurants);        assertEquals(OrderValidationCode.PIZZA_FROM_MULTIPLE_RESTAURANTS, order.getOrderValidationCode());    }}