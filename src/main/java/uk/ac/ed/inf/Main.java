package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.constant.OrderStatus;

public class Main {
    public static void main( String[] args )
    {
        System.out.println( "Small test for the IlpDataObjects!" );
        OrderStatus orderStatus = OrderStatus.DELIVERED;
        System.out.println(orderStatus);
        System.out.println("IlpDataObjects.jar is working!");
    }
}
