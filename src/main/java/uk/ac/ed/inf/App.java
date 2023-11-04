package uk.ac.ed.inf;

import uk.ac.ed.inf.ilp.data.Order;
import uk.ac.ed.inf.controller.DeliveryController;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class App {

    public static void main(String[] args){
        // check that there are the correct number of arguments
        if (args.length != 2){
            System.err.println("Incorrect number of arguments, expected 2 got " + args.length);
        } else {
            // set the url and date
            var date = args[0];
            var url = args[1];
            // validate the date and url
            try {
                LocalDate parsed = LocalDate.parse(date);
                if (isValidURL(url)){
                    // construct the deliverorders object, and start execution
//                    TODO: uncomment this line
//                    DeliveryController main = new DeliveryController(date, url);
//                    main.run();
                }
            } catch (DateTimeParseException e){
                System.err.println(e);
                System.err.println("Invalid date.");
            } catch (MalformedURLException | URISyntaxException e){
                System.err.println(e);
                System.err.println("Invalid url.");
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
