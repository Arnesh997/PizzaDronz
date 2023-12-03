package uk.ac.ed.inf;

import uk.ac.ed.inf.Client.RestClient;
import uk.ac.ed.inf.Controller.DeliveryController;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
/**
 * App entry point
 */
public class App {
    /**
     * Entry point into the program
     * @param args command line arguments
     */
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Incorrect number of arguments, expected 2 got " + args.length);
            System.err.println("Usage: java -jar PizzaDronz - 1.0-SNAPSHOT.jar <date> <url>");
            System.exit(1); // Exiting gracefully
        } else {
            var date = args[0];
            var url = args[1];

            if (!isValidDate(date)) {
                System.err.println("Invalid date");
                System.exit(1);
            }
            if(url == null || !isValidURL(url)){
                System.err.println("Invalid URL");
                System.exit(1); // Exiting gracefully
            }
            RestClient restClient = new RestClient(url);
            if(!restClient.isAlive()){
                System.err.println("REST Server is not alive");
                System.exit(1);
            }

            try {
                LocalDate parsedDate = LocalDate.parse(date);
                DeliveryController main = new DeliveryController(parsedDate, url);
                main.run();
            } catch (Exception e) { // Catching any unexpected exceptions
                System.out.println("An error occurred: " + e.getMessage());
                System.exit(1);
            }
        }
    }

    /**
     * Checks if the date is in the correct format
     * @param date the date to be checked
     * @return true if the date is in the correct format, false otherwise
     */
    private static boolean isValidDate(String date) {
        try {
            LocalDate.parse(date);
            return true;
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format: Expected YYYY-MM-DD. Actual " + date);
            System.err.println("Error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Checks if the URL is in the correct format
     * @param url the URL to be checked
     * @return true if the URL is in the correct format, false otherwise
     */
    private static boolean isValidURL(String url) {
        try {
            new URL(url).toURI();
            return true;
        } catch (MalformedURLException | URISyntaxException e) {
            System.out.println("Invalid URL format: " + e.getMessage());
            System.exit(1);
        }
        return false;
    }
}

