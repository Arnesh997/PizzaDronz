package uk.ac.ed.inf.Data;

import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.controller.DeliveryController;

import java.util.List;

public class App {

    public static void main(String[] args) {
        // Check if the correct number of arguments are passed
        if (args.length != 2) {
            System.out.println("Usage: java -jar PizzaDronz-1.0-SNAPSHOT.jar <date> <REST-server-URL>");
            System.exit(1);
        }

        // Get the date and REST server URL from command-line arguments
        String date = args[0];
        String baseUrl = args[1];

        // Instantiate the DeliveryHandler with the base URL
        DeliveryController deliveryController = new DeliveryController(baseUrl);

        // Fetch orders for the specified date
        List<Order> orders = deliveryController.getOrdersForDate(date);
        if (orders == null) {
            System.out.println("Failed to fetch orders or no orders found for the specified date.");
            System.exit(2);
        }

        // Validate the orders
        boolean areOrdersValid = deliveryController.validateOrders(orders);
        if (!areOrdersValid) {
            System.out.println("Validation failed for one or more orders.");
            System.exit(3);
        }

        // If validation passes, proceed with further processing (not shown)
        // ...

        System.out.println("All orders for " + date + " are valid.");
    }
}
