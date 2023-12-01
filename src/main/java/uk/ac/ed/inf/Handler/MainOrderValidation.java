package uk.ac.ed.inf.Handler;import uk.ac.ed.inf.ilp.constant.OrderStatus;import uk.ac.ed.inf.ilp.constant.OrderValidationCode;import uk.ac.ed.inf.ilp.data.CreditCardInformation;import uk.ac.ed.inf.ilp.data.Order;import uk.ac.ed.inf.ilp.data.Pizza;import uk.ac.ed.inf.ilp.data.Restaurant;import uk.ac.ed.inf.ilp.interfaces.OrderValidation;import java.time.DayOfWeek;import java.time.LocalDate;import java.time.YearMonth;import java.time.format.DateTimeFormatter;import java.util.HashMap;import java.util.HashSet;import java.util.Set;import static uk.ac.ed.inf.ilp.constant.SystemConstants.MAX_PIZZAS_PER_ORDER;//Things to Validate:// 1. Number of pizzas in the order// 2. Credit card info - Expiry Date// 3. Restaurant - Opening Days with the order date// 4. Total Price in Pence: Note - 100pence added for delivery for all orders// 5. Past Orders are not to be considered// 6. You have to check card number, expiration, order date and items among other details (as described in this document)// 7. All items in an order must come from the same pizza restaurant and are valid pizzas// 8. Total Price validation. (Price +100pence for delivery), Total > 0// 9. Validate past and future order datepublic class MainOrderValidation implements OrderValidation{    HashMap<String,String> pizzaRestaurantMap = new HashMap<>();    HashMap<String, Integer> pizzaPriceMap = new HashMap<>();    HashMap<String, HashSet<DayOfWeek>> restaurantOpeningDaysMap = new HashMap<>();    /**     * validate an order and deliver a validated version where the     * OrderStatus and OrderValidationCode are set accordingly.     *     * The order validation code is defined in the enum @link uk.ac.ed.inf.ilp.constant.OrderValidationStatus     *     * <p>     * Fields to validate include (among others - for details please see the OrderValidationStatus):     * <p>     * number (16 digit numeric)     * CVV     * expiration date     * the menu items selected in the order     * the involved restaurants     * if the maximum count is exceeded     * if the order is valid on the given date for the involved restaurants (opening days)     *     * @param orderToValidate    is the order which needs validation     * @param definedRestaurants is the vector of defined restaurants with their according menu structure     * @return the validated order after setting the necessary status and validation code     */    @Override    public Order validateOrder(Order orderToValidate, Restaurant[] definedRestaurants) {        // Variables for the orderToValidate        Pizza[] pizzasInOrder = orderToValidate.getPizzasInOrder();        LocalDate orderDate = orderToValidate.getOrderDate();        int totalPrice = orderToValidate.getPriceTotalInPence();        setup(definedRestaurants);        // Variables for credit card validation        CreditCardInformation cardDetails = orderToValidate.getCreditCardInformation();//      Credit Card Validation (Card Number, Expiry Date, CVV)        boolean checkCardNumber = cardNumberCheck(cardDetails);        if (!checkCardNumber) {            orderToValidate.setOrderStatus(OrderStatus.INVALID);            orderToValidate.setOrderValidationCode(OrderValidationCode.CARD_NUMBER_INVALID);            return orderToValidate;        }        boolean checkCardExpiry = cardExpiryCheck(cardDetails, orderDate);        if (!checkCardExpiry) {            orderToValidate.setOrderStatus(OrderStatus.INVALID);            orderToValidate.setOrderValidationCode(OrderValidationCode.EXPIRY_DATE_INVALID);            return orderToValidate;        }        boolean checkCardCvv = cardCvvCheck(cardDetails);        if (!checkCardCvv) {            orderToValidate.setOrderStatus(OrderStatus.INVALID);            orderToValidate.setOrderValidationCode(OrderValidationCode.CVV_INVALID);            return orderToValidate;        }//      NUMBER OF PIZZAS in the order check        boolean pizzaCountCheck = countCheck(pizzasInOrder);        if (!pizzaCountCheck) {            orderToValidate.setOrderStatus(OrderStatus.INVALID);            orderToValidate.setOrderValidationCode(OrderValidationCode.MAX_PIZZA_COUNT_EXCEEDED);            return orderToValidate;        }//      PIZZA_DEFINED & PIZZA_FROM_MULTIPLE_RESTAURANTS in the order check//      If the pizza name is not in the hashmap, then the pizza is not defined//      If ordered pizzas match with multiple restaurants, then PIZZA_FROM_MULTIPLE_RESTAURANTS        boolean pizzaDefined = false;        pizzaDefined = pizzaDefineCheck(pizzasInOrder);        if(!pizzaDefined){            orderToValidate.setOrderStatus(OrderStatus.INVALID);            orderToValidate.setOrderValidationCode(OrderValidationCode.PIZZA_NOT_DEFINED);            return orderToValidate;        }        boolean pizzaFromMultipleRestaurants = false;        pizzaFromMultipleRestaurants = pizzaMultipleRestaurantsCheck(pizzasInOrder);        if(pizzaFromMultipleRestaurants){            orderToValidate.setOrderStatus(OrderStatus.INVALID);            orderToValidate.setOrderValidationCode(OrderValidationCode.PIZZA_FROM_MULTIPLE_RESTAURANTS);            return orderToValidate;        }//      RESTAURANT_CLOSED on the order date check//      Using a hashmap to map the restaurant name to the opening days        boolean restaurantClosed = restaurantClosedCheck(pizzasInOrder[0].name(),orderDate);        if(restaurantClosed){            orderToValidate.setOrderStatus(OrderStatus.INVALID);            orderToValidate.setOrderValidationCode(OrderValidationCode.RESTAURANT_CLOSED);            return orderToValidate;        }//      Total Price in Pence Validation        boolean totalPriceValidation = totalPriceCheck(pizzasInOrder, totalPrice);        if(!totalPriceValidation){            orderToValidate.setOrderStatus(OrderStatus.INVALID);            orderToValidate.setOrderValidationCode(OrderValidationCode.TOTAL_INCORRECT);            return orderToValidate;        }//      ASSIGNING ORDER VALIDATION CODE TO THE GIVEN ORDER: 'orderToValidate'        OrderValidationCode code = OrderValidationCode.NO_ERROR;//      Current Order date check - Required for validation code VALID_BUT_NOT_DELIVERED        boolean currentOrderDate = currentOrderDateCheck(orderDate);//      ORDER STATUS CODE ASSIGNMENT        OrderStatus status = OrderStatus.UNDEFINED;        if(code == OrderValidationCode.NO_ERROR){            orderToValidate.setOrderValidationCode(OrderValidationCode.NO_ERROR);            orderToValidate.setOrderStatus(OrderStatus.VALID_BUT_NOT_DELIVERED);            return orderToValidate;        }        return orderToValidate;    }    /**     * This method is used to initialise the hashmaps which are used extensively in the validation process     * @param definedRestaurants is array of restaurants which are defined in the system     */    public void setup(Restaurant[] definedRestaurants){        // Using a hashmap to map the pizza name to the restaurant name        for(Restaurant rest : definedRestaurants) {            for(Pizza pizza: rest.menu()){                if(pizza != null) {                    pizzaRestaurantMap.put(pizza.name(), rest.name());                }                else {                    continue;                }            }        }        // Mapping pizza name to their prices        for(Restaurant rest : definedRestaurants) {            for(Pizza pizza: rest.menu()){                if(pizza != null) {                    pizzaPriceMap.put(pizza.name(), pizza.priceInPence());                }                else {                    continue;                }            }        }//      Using a hashmap to map the restaurant name to the opening days        for(Restaurant rest: definedRestaurants ){            Set<DayOfWeek> openingDays = new HashSet<>();            for(DayOfWeek day: rest.openingDays()){                if(day != null) {                    openingDays.add(day);                }                else {                    continue;                }            }            restaurantOpeningDaysMap.put(rest.name(), (HashSet<DayOfWeek>) openingDays);        }    }    /**    * Returns True or False if the number of pizzas in the order is greater than 4    * @param pizzasInOrder is the array of pizzas in the order    */    public boolean countCheck(Pizza[] pizzasInOrder){        if(pizzasInOrder == null){            return false;        }        if(pizzasInOrder.length > MAX_PIZZAS_PER_ORDER || pizzasInOrder.length == 0){            return false;        }        else{            return true;        }    }    /**     * Returns True if the order date is same as current date, otherwise False     * @param orderDate is the date of the order     */    protected boolean currentOrderDateCheck(LocalDate orderDate){        LocalDate currentDate = LocalDate.now();        return orderDate.isEqual(currentDate);    }    /**     * Returns True if the order date is after the current date, otherwise False     * @param orderDate is the date of the order     */    protected boolean futureOrderDateCheck(LocalDate orderDate){        LocalDate currentDate = LocalDate.now();        return orderDate.isAfter(currentDate);    }    /**     * Returns True if the order date is before the current date, otherwise False     * @param orderDate is the date of the order     */    protected boolean pastOrderDateCheck(LocalDate orderDate){        LocalDate currentDate = LocalDate.now();        return orderDate.isBefore(currentDate);    }/**     * Returns True if the restaurant is closed on the orderDate's day of the week, otherwise False     * @param pizzaName is the name of the pizza     * @param orderDate is the date of the order     */public boolean restaurantClosedCheck(String pizzaName, LocalDate orderDate){        String restaurantName = pizzaRestaurantMap.get(pizzaName);        HashSet<DayOfWeek> openingDays = restaurantOpeningDaysMap.get(restaurantName);        if(openingDays.contains(orderDate.getDayOfWeek())){            return false;        }        else{            return true;        }    }    /**     * Returns True if all the pizzas in the order are present in any of the menu of the restaurants, otherwise False     * @param pizzasInOrder is the array of pizzas in the order     */    public boolean pizzaDefineCheck(Pizza[] pizzasInOrder){        if(pizzasInOrder == null || pizzasInOrder.length == 0){            return false;        }        for(Pizza pizza: pizzasInOrder){            if(pizza == null || pizza.name() == null){                return false;            }            if(pizzaRestaurantMap.get(pizza.name()) == null){                return false;            }        }        return true;    }    /**     * Returns True if the pizza in the order are from multiple restaurants, otherwise False     * @param pizzasInOrder is the array of pizzas in the order     */    public boolean pizzaMultipleRestaurantsCheck(Pizza[] pizzasInOrder){        HashSet<String> restaurantSet = new HashSet<>();        for(Pizza pizza: pizzasInOrder){            restaurantSet.add(pizzaRestaurantMap.get(pizza.name()));        }        if(restaurantSet.size() > 1){            return true;        }        else {            return false;        }    }    /**     * Returns True if the total price of the pizzas along with the 100 pence delivery charge in the order is correct, otherwise False     * @param pizzasInOrder is the array of pizzas in the order     * @param totalPrice is the total price of the order     */    public boolean totalPriceCheck(Pizza[] pizzasInOrder, int totalPrice){//      Check if the price of the pizza in the given order is correct        int priceTotal = 0;        for (Pizza pizza : pizzasInOrder) {            priceTotal += pizzaPriceMap.get(pizza.name());        }        return ((priceTotal > 0) && (priceTotal+100 == totalPrice));    }    /**     * Returns True if the card number is valid, otherwise False     * @param cardDetail is the credit card information     */    public boolean cardNumberCheck(CreditCardInformation cardDetail){        String cardNumber = cardDetail.getCreditCardNumber();        if (cardNumber == null || cardNumber.length() != 16) {            return false;        }        if (!cardNumber.matches("\\d+")) {            // checks if the card number contains only digits            return false;        }        return true;    }    /**     * Returns True if the card CVV is valid, otherwise False     * @param cardDetail is the credit card information     */    public boolean cardCvvCheck(CreditCardInformation cardDetail){        String cardCvv = cardDetail.getCvv();        if (cardCvv == null || cardCvv.length() != 3) {            return false;        }        if (!cardCvv.matches("\\d+")) { // checks if the card number contains only digits            return false;        }        return true;    }    /**     * Returns True if the card expiry date is valid, otherwise False     * @param cardDetail is the credit card information     */    public boolean cardExpiryCheck(CreditCardInformation cardDetail, LocalDate orderDate) {        String cardExpiryDate = cardDetail.getCreditCardExpiry();        // Check if expiryDate is not null        if (cardExpiryDate == null) {            return false;        }        // Parse the expiry date        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yy");        YearMonth expDate;        try {            expDate = YearMonth.parse(cardExpiryDate, formatter);        } catch (Exception e) {            return false;        }        // Compare the expiry year and month with the order year and month        // Credit card is valid through the end of the month of expiry        LocalDate lastDayOfExpiryMonth = expDate.atEndOfMonth();        return !orderDate.isAfter(lastDayOfExpiryMonth);    }}