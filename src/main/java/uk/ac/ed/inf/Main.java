package uk.ac.ed.inf;

import uk.ac.ed.inf.Controller.DeliveryController;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
/**
 * Main entry point
 */
public class Main {
    /**
     * Entry point into the program
     * @param args command line arguments
     */
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Incorrect number of arguments, expected 2 got " + args.length);
            System.exit(1); // Exiting gracefully
        } else {
            String date = args[0];
            String url = args[1];

            if (!isValidDate(date) || !isValidURL(url)) {
                System.exit(2); // Exiting gracefully
            }

            try {
                LocalDate parsedDate = LocalDate.parse(date);
                DeliveryController main = new DeliveryController(parsedDate, url);
                main.run();
            } catch (Exception e) { // Catching any unexpected exceptions
                System.out.println("An unexpected error occurred: " + e.getMessage());
                System.exit(3); // Exiting gracefully
            }
        }
    }

    private static boolean isValidDate(String date) {
        try {
            LocalDate.parse(date);
            return true;
        } catch (DateTimeParseException e) {
            System.out.println("Invalid date format: " + e.getMessage());
            return false;
        }
    }

    private static boolean isValidURL(String url) {
        try {
            new URL(url).toURI();
            return true;
        } catch (MalformedURLException | URISyntaxException e) {
            System.out.println("Invalid URL format: " + e.getMessage());
            return false;
        }
    }
}

