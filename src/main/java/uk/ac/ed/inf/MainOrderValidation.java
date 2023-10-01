package uk.ac.ed.inf;import uk.ac.ed.inf.ilp.constant.OrderStatus;import uk.ac.ed.inf.ilp.constant.OrderValidationCode;import uk.ac.ed.inf.ilp.constant.SystemConstants;import uk.ac.ed.inf.ilp.data.CreditCardInformation;import uk.ac.ed.inf.ilp.data.Order;import uk.ac.ed.inf.ilp.data.Pizza;import uk.ac.ed.inf.ilp.data.Restaurant;import uk.ac.ed.inf.ilp.interfaces.OrderValidation;import java.time.DayOfWeek;import java.time.LocalDate;import java.time.LocalDateTime;import java.time.YearMonth;import java.time.format.DateTimeFormatter;import java.util.HashMap;import java.util.HashSet;import java.util.Set;import static uk.ac.ed.inf.ilp.constant.SystemConstants.MAX_PIZZAS_PER_ORDER;//Things to Validate:// 1. Number of pizzas in the order// 2. Credit card info - Expiry Date// 3. Restaurant - Opening Days with the order date// 4. Total Price in Pence: Note - 100pence added for delivery for all orders// 5. Past Orders are not to be considered// 6. You have to check card number, expiration, order date and items among other details (as described in this document)// 7. All items in an order must come from the same pizza restaurant and are valid pizzas// 8. Total Price validation. (Price +100pence for delivery), Total > 0// 9. Validate past and future order datepublic class MainOrderValidation implements OrderValidation{    static HashMap<String,String> pizzaRestaurantMap;    static HashMap<String, Integer> pizzaPriceMap;    static HashMap<String, HashSet<DayOfWeek>> restaurantOpeningDaysMap;    @Override    public Order validateOrder(Order orderToValidate, Restaurant[] definedRestaurants) {        // Variables for the orderToValidate        Pizza[] pizzasInOrder = orderToValidate.getPizzasInOrder();        LocalDate orderDate = orderToValidate.getOrderDate();        int totalPrice = orderToValidate.getPriceTotalInPence();        setup(definedRestaurants);        // Variables for credit card validation        CreditCardInformation cardDetails = orderToValidate.getCreditCardInformation();//      NUMBER OF PIZZAS in the order check        boolean pizzaCountCheck = countCheck(pizzasInOrder);//      Credit Card Validation        boolean checkCardNumber = cardNumberCheck(cardDetails);        boolean checkCardExpiry = cardExpiryCheck(cardDetails);        boolean checkCardCvv = cardCvvCheck(cardDetails);//      Total Price in Pence Validation        boolean totalPriceValidation = totalPriceCheck(pizzasInOrder, totalPrice);//      PIZZA_DEFINED & PIZZA_FROM_MULTIPLE_RESTAURANTS in the order check//      If the pizza name is not in the hashmap, then the pizza is not defined//      If ordered pizzas match with multiple restaurants, then PIZZA_FROM_MULTIPLE_RESTAURANTS        boolean pizzaDefined = false;        boolean pizzaFromMultipleRestaurants = false;        pizzaDefined = pizzaDefineCheck(pizzasInOrder);        pizzaFromMultipleRestaurants = pizzaMultipleRestaurantsCheck(pizzasInOrder);//       RESTAURANT_CLOSED on the order date check//       Using a hashmap to map the restaurant name to the opening days        restaurantOpeningDaysMap = new HashMap<>();        boolean restaurantClosed = restaurantClosedCheck(orderDate);//      ORDER DATE CHECKS//      Past Order date check - Required for validation code DELIVERED and along with credit card validation        boolean pastOrderDate = pastOrderDateCheck(orderDate);//      Future Order date check  - Required for validation code INVALID        boolean futureOrderDate = futureOrderDateCheck(orderDate);//      Current Order date check - Required for validation code VALID_BUT_NOT_DELIVERED        boolean currentOrderDate = currentOrderDateCheck(orderDate);//      ASSIGNING ORDER VALIDATION CODE TO THE GIVEN ORDER: 'orderToValidate'        OrderValidationCode code = OrderValidationCode.UNDEFINED;        if(!pizzaCountCheck){            code = OrderValidationCode.MAX_PIZZA_COUNT_EXCEEDED;        }        else if(!checkCardNumber){            code = OrderValidationCode.CARD_NUMBER_INVALID;        }        else if(!checkCardExpiry){            code = OrderValidationCode.EXPIRY_DATE_INVALID;        }        else if(!checkCardCvv){            code = OrderValidationCode.CVV_INVALID;        }        else if(!totalPriceValidation){            code = OrderValidationCode.TOTAL_INCORRECT;        }        else if(!pizzaDefined){            code = OrderValidationCode.PIZZA_NOT_DEFINED;        }        else if(pizzaFromMultipleRestaurants){            code = OrderValidationCode.PIZZA_FROM_MULTIPLE_RESTAURANTS;        }        else if(restaurantClosed){            code = OrderValidationCode.RESTAURANT_CLOSED;        }        else{            code = OrderValidationCode.NO_ERROR;        }//      ORDER STATUS CODE ASSIGNMENT        OrderStatus status = OrderStatus.UNDEFINED;        if(code == OrderValidationCode.NO_ERROR && currentOrderDate){            status = OrderStatus.VALID_BUT_NOT_DELIVERED;        }        else if(code == OrderValidationCode.NO_ERROR && (futureOrderDate || pastOrderDate)){            status = OrderStatus.INVALID;        }        else{            status = OrderStatus.INVALID;        }        orderToValidate.setOrderStatus(status);        orderToValidate.setOrderValidationCode(code);        return orderToValidate;    }    protected boolean countCheck(Pizza[] pizzasInOrder){        if(pizzasInOrder.length > MAX_PIZZAS_PER_ORDER || pizzasInOrder.length == 0){            return false;        }        else{            return true;        }    }    protected boolean currentOrderDateCheck(LocalDate orderDate){        LocalDate currentDate = LocalDate.now();        return orderDate.isEqual(currentDate);    }    protected boolean futureOrderDateCheck(LocalDate orderDate){        LocalDate currentDate = LocalDate.now();        return orderDate.isAfter(currentDate);    }    protected boolean pastOrderDateCheck(LocalDate orderDate){        LocalDate currentDate = LocalDate.now();        return orderDate.isBefore(currentDate);    }    protected boolean restaurantClosedCheck(LocalDate orderDate){        String restaurantName = "";        for(String pizzaName: pizzaRestaurantMap.keySet()){            restaurantName = pizzaRestaurantMap.get(pizzaName);        }        HashSet<DayOfWeek> openingDays = restaurantOpeningDaysMap.get(restaurantName);        if(openingDays.contains(orderDate.getDayOfWeek())){            return false;        }        else{            return true;        }    }    protected boolean pizzaDefineCheck(Pizza[] pizzasInOrder){        int count = 0;        for(Pizza pizza: pizzasInOrder){            if(pizzaRestaurantMap.containsKey(pizza.name())){                count++;            }        }        return count == pizzasInOrder.length;    }    protected boolean pizzaMultipleRestaurantsCheck(Pizza[] pizzasInOrder){        HashSet<String> restaurantSet = new HashSet<>();        for(Pizza pizza: pizzasInOrder){            restaurantSet.add(pizzaRestaurantMap.get(pizza.name()));        }        if(restaurantSet.size() > 1){            return true;        }        else {            return false;        }    }    protected boolean totalPriceCheck(Pizza[] pizzasInOrder, int totalPrice){//      Check if the price of the pizza in the given order is correct        boolean priceCheck = priceMatch(pizzasInOrder);        int priceTotal = 0;        for (Pizza pizza : pizzasInOrder) {            priceTotal += pizza.priceInPence();        }        return ((priceTotal > 0) && (priceTotal+100 == totalPrice) && priceCheck);    }    protected boolean priceMatch(Pizza[] pizzasInOrder){//  Checking if the pizzas in the order match with the menu prices for a specific restaurant using pizzaPriceMap        boolean priceMatch = true;        for(Pizza pizza: pizzasInOrder){            if(pizzaRestaurantMap.containsKey(pizza.name())){                if(pizza.priceInPence() == pizzaPriceMap.get(pizza.name())){                    priceMatch = true;                }                else{                    return false;                }            }        }        return priceMatch;    }//  Card Number Check: Length  check    protected boolean cardNumberCheck(CreditCardInformation cardDetail){        String cardNumber = cardDetail.getCreditCardNumber();        if (cardNumber == null || cardNumber.length() != 16) {            return false;        }        if (!cardNumber.matches("\\d+")) { // checks if the card number contains only digits            return false;        }        return true;    }//    Card CVV check    protected boolean cardCvvCheck(CreditCardInformation cardDetail){        String cardCvv = cardDetail.getCvv();        if (cardCvv == null || cardCvv.length() != 3) {            return false;        }        if (!cardCvv.matches("\\d+")) { // checks if the card number contains only digits            return false;        }        return true;    }//    Card Expiry Check    protected boolean cardExpiryCheck(CreditCardInformation cardDetail){        String cardExpiryDate = cardDetail.getCreditCardExpiry();        // Check if expiryDate is not null        if (cardExpiryDate == null) {            return false;        }        // Parse the expiry date        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yy");        YearMonth expDate;        try {            expDate = YearMonth.parse(cardExpiryDate, formatter);        } catch (Exception e) {            return false;        }        // Compare the expiry year and month with the current year and month        return !YearMonth.now().isAfter(expDate);    }    public static void setup(Restaurant[] definedRestaurants){        // Using a hashmap to map the pizza name to the restaurant name        pizzaRestaurantMap = new HashMap<>();        for(Restaurant rest : definedRestaurants) {            for(Pizza pizza: rest.menu()){                pizzaRestaurantMap.put(pizza.name(), rest.name());            }        }        // Mapping pizza name to their prices        pizzaPriceMap = new HashMap<>();        for(Restaurant rest : definedRestaurants) {            for(Pizza pizza: rest.menu()){                pizzaPriceMap.put(pizza.name(), pizza.priceInPence());            }        }//      Using a hashmap to map the restaurant name to the opening days        restaurantOpeningDaysMap = new HashMap<>();        for(Restaurant rest: definedRestaurants ){            Set<DayOfWeek> openingDays = new HashSet<>();            for(DayOfWeek day: rest.openingDays()){                openingDays.add(day);            }            restaurantOpeningDaysMap.put(rest.name(), (HashSet<DayOfWeek>) openingDays);        }    }}