package uk.ac.ed.inf.controller;import uk.ac.ed.inf.*;import uk.ac.ed.inf.ilp.constant.OrderStatus;import uk.ac.ed.inf.ilp.data.*;import java.time.LocalDate;import java.util.ArrayList;import java.util.Collections;import java.util.HashMap;import java.util.List;public class DeliveryController {    private  Restaurant[] restaurants;    private   List<NamedRegion> noFlyZones;    private  NamedRegion centralArea;    private  List<Order> orders;    private  String BASE_URL;    private  LocalDate date;    private HashMap<String,LngLat> pizzaLocationMap= new HashMap<>();    private LngLat appletonTower = new LngLat(-3.1869, 55.9445);    public DeliveryController(LocalDate date, String BASE_URL){        this.date = date;        this.BASE_URL = BASE_URL;        RestClientController restClientController = new RestClientController(BASE_URL);        this.restaurants = restClientController.getRestaurants();        this.noFlyZones = restClientController.getNoFlyZones();        this.centralArea = restClientController.getCentralArea();        this.orders = restClientController.getOrders();        for(Restaurant restaurant: restaurants){            for(Pizza pizza: restaurant.menu()){                pizzaLocationMap.put(pizza.name(),restaurant.location());            }        }    }    public void run(){        // Order controlling code        RestClientController restClientController = new RestClientController(BASE_URL);        orders = restClientController.getOrders(date.toString());        List<Order> allOrdersToday = checkOrdersAll(this.orders);        System.out.println("All orders today: "+allOrdersToday.size());        List<Order> validOrders = checkOrders(this.orders);        System.out.println("Valid orders today: "+validOrders.size());//      Resetting status code of all valid orders -- Need to remove        for(Order order: validOrders){            order.setOrderStatus(OrderStatus.DELIVERED);        }        DroneDeliveryRecorder droneDeliveryRecorder = new DroneDeliveryRecorder(LocalDate.now());        droneDeliveryRecorder.createDeliveriesFile(allOrdersToday);        // Path-finding code        AStarPathController pathController = new AStarPathController(this.restaurants, this.noFlyZones, this.centralArea);        List<List<DroneMove>> combinedDroneMovesPath = new ArrayList<>();        for(Order order : validOrders){            LngLat restaurantLocation = pizzaLocationMap.get(order.getPizzasInOrder()[0].name());            List<Node> pathAppletonRestaurant = pathController.findPath(appletonTower, restaurantLocation);            System.out.println("Path from Appleton Tower to Restaurant: " + pathAppletonRestaurant.size());            pathAppletonRestaurant.add(new Node(pathAppletonRestaurant.get(pathAppletonRestaurant.size()-1),pathAppletonRestaurant.get(pathAppletonRestaurant.size()-1).getLocation(),0,0,999.0 ));            // Create a reversed path for the return journey            List<Node> pathRestaurantAppleton = new ArrayList<>(pathAppletonRestaurant);            Collections.reverse(pathRestaurantAppleton);            pathRestaurantAppleton.remove(0);            pathRestaurantAppleton.add(new Node(pathRestaurantAppleton.get(pathRestaurantAppleton.size()-1),pathRestaurantAppleton.get(pathRestaurantAppleton.size()-1).getLocation(),0,0,999.0));            // Reverse the angles for the return path            for (Node node : pathRestaurantAppleton) {                double reversedAngle = (node.getAngle() + 180) % 360;                node.setAngle(reversedAngle);            }            List<Node> combinedNodePath = new ArrayList<>(pathAppletonRestaurant);            combinedNodePath.addAll(pathRestaurantAppleton);//          Converting Nodes in the path to drone moves            combinedDroneMovesPath.add(convertNodesToMoves(combinedNodePath, order.getOrderNo()));        }        droneDeliveryRecorder.createFlightPathFile(combinedDroneMovesPath);        droneDeliveryRecorder.createGeoJsonFile(combinedDroneMovesPath);    }    private List<Order> checkOrdersAll(List<Order> orders){        List<Order> allOrders = new ArrayList<>();        MainOrderValidation orderValidator = new MainOrderValidation();        for(Order order: orders){            Order checkedOrder = orderValidator.validateOrder(order, this.restaurants);            if (checkedOrder.getOrderStatus() == OrderStatus.VALID_BUT_NOT_DELIVERED) {                checkedOrder.setOrderStatus(OrderStatus.DELIVERED); // Only add valid orders to the new list            }            allOrders.add(checkedOrder);        }        return allOrders;    }    private List<Order> checkOrders(List<Order> orders) {        List<Order> validOrders = new ArrayList<>();        MainOrderValidation orderValidator = new MainOrderValidation();        for (Order order : orders) {            Order checkedOrder = orderValidator.validateOrder(order, this.restaurants);            if (checkedOrder.getOrderStatus() == OrderStatus.VALID_BUT_NOT_DELIVERED) {                validOrders.add(order); // Only add valid orders to the new list            }        }        return validOrders;    }    private List<DroneMove> convertNodesToMoves(List<Node> path, String orderNo) {        List<DroneMove> moves = new ArrayList<>();        for (int i = 0; i < path.size() - 1; i++) {            Node start = path.get(i);            Node end = path.get(i + 1);            double angle = start.getAngle();            DroneMove move = new DroneMove(orderNo, start.getLocation().lng(),                    start.getLocation().lat(), angle,                    end.getLocation().lng(), end.getLocation().lat());            moves.add(move);        }        return moves;    }}