package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.constant.OrderStatus;
import uk.ac.ed.inf.ilp.data.LngLat;
import uk.ac.ed.inf.ilp.data.NamedRegion;
import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.ilp.data.Restaurant;
import uk.ac.ed.inf.ilp.interfaces.*;
public class Main implements LngLatHandling, OrderValidation
{
    public static void main( String[] args )
    {
        System.out.println( "Small test for the IlpDataObjects!" );
        OrderStatus orderStatus = OrderStatus.DELIVERED;
        System.out.println(orderStatus);
        System.out.println("IlpDataObjects.jar is working!");
    }

    @Override
    public double distanceTo(LngLat startPosition, LngLat endPosition) {
        return 0;
    }

    @Override
    public boolean isCloseTo(LngLat startPosition, LngLat otherPosition) {
        return false;
    }

    @Override
    public boolean isInRegion(LngLat position, NamedRegion region) {
//        Raycasting algorithm
        return false;
    }

    @Override
    public LngLat nextPosition(LngLat startPosition, double angle) {
        return null;
    }

    @Override
    public Order validateOrder(Order orderToValidate, Restaurant[] definedRestaurants) {
        return null;
    }
}
