package uk.ac.ed.inf.Command;

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
    public static void main(String[] args){
        // check that there are the correct number of arguments
        if (args.length != 2){
            System.out.println("Incorrect number of arguments, expected 2 got " + args.length);
        } else {
            // set the url and date
            var date = args[0];
            var url = args[1];
            // validate the date and url
            try {
                LocalDate parsedDate = LocalDate.parse(date);
                if (isValidURL(url)){
                    // construct the DeliveryController object, and start execution
                    DeliveryController main = new DeliveryController(parsedDate, url);
                    main.run();
                }
            } catch (DateTimeParseException e){
                System.out.println(e);
                System.out.println("Invalid date.");
            } catch (MalformedURLException | URISyntaxException e){
                System.out.println(e);
                System.out.println("Invalid url.");
            }
        }
    }

    private static boolean isValidURL(String url) throws MalformedURLException, URISyntaxException {
        try {
            new URL(url).toURI();
            return true;
        } catch (MalformedURLException e) {
            return false;
        } catch (URISyntaxException e) {
            return false;
        }
    }
}
