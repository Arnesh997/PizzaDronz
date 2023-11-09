package uk.ac.ed.inf;import uk.ac.ed.inf.ilp.data.LngLat;import uk.ac.ed.inf.ilp.data.NamedRegion;import uk.ac.ed.inf.ilp.data.Order;import uk.ac.ed.inf.ilp.data.Restaurant;import java.util.*;import static uk.ac.ed.inf.ilp.constant.SystemConstants.DRONE_MOVE_DISTANCE;public class AStarPathControl {    private NamedRegion centralArea;    private List<NamedRegion> noFlyZones;    private Restaurant[] restaurants;    private Order order;    private Node endNode;    MainLngLatHandle lngLatHandle = new MainLngLatHandle();    public AStarPathControl(Restaurant[] restaurants, List<NamedRegion> noFlyZones, NamedRegion centralArea) {        this.restaurants = restaurants;        this.noFlyZones = noFlyZones;        this.centralArea = centralArea;        this.endNode = new Node(null, new LngLat(-3.1869, 55.9445), 0, 0, 0);    }    public  List<Node> findPath(LngLat start) {        Node startNode = new Node(null, start, 0, 0, 0);        PriorityQueue<Node> openSet = new PriorityQueue<>(Comparator.comparingDouble(Node::getFCost));        Set<LngLat> closedSet = new HashSet<>();        openSet.add(startNode);        while (!openSet.isEmpty()) {            Node currentNode = openSet.poll();            if (currentNode.getLocation().equals(endNode.getLocation())) {                return constructPath(currentNode);            }            closedSet.add(currentNode.getLocation());            for (Node neighbor : getNeighbors(currentNode)) {                if (closedSet.contains(neighbor.getLocation())) {                    continue;                }                double tentativeGCost = currentNode.getGCost() + getDistance(currentNode, neighbor);                if (tentativeGCost < neighbor.getGCost()) {                    neighbor.setParent(currentNode);                    neighbor.setgCost(tentativeGCost);                    neighbor.setfCost(tentativeGCost + neighbor.getHCost());                    if (openSet.stream().noneMatch(n -> n.getLocation().equals(neighbor.getLocation()))) {                        openSet.add(neighbor);                    }                }            }        }        return new ArrayList<>(); // Return an empty path if there is no path from start to end    }    private List<Node> constructPath(Node endNode) {        List<Node> path = new ArrayList<>();        Node currentNode = endNode;        while (currentNode != null) {            path.add(currentNode);            currentNode = currentNode.getParent();        }        Collections.reverse(path);        return path;    }    // Implement this method to generate neighbor nodes, taking into account no-fly zones and other constraints.//    private List<Node> getNeighbors(Node node) {//        List<Node> neighbors = new ArrayList<>();//        // Define the angles for possible movements//        double[] angles = {0, 22.5, 45, 67.5, 90, 112.5, 135, 157.5, 180, 202.5, 225, 247.5, 270, 292.5, 315, 337.5};////        // Get the current location//        LngLat currentLocation = node.getLocation();////        // Check if current position is within the central area//        boolean currPosInCentral = lngLatHandle.isInRegion(currentLocation, centralArea);////        for (double angle : angles) {//            LngLat nextPos = lngLatHandle.nextPosition(currentLocation, angle);//            if (!lngLatHandle.isInRegion(nextPos, noFlyZones) && (currPosInCentral == lngLatHandle.isInRegion(nextPos, centralArea))) {//                double gCost = node.getGCost() + lngLatHandle.distanceTo(currentLocation, nextPos);//                double hCost = calculateHeuristic(nextPos, endNode.getLocation());////                Node neighbor = new Node(node, nextPos, gCost, hCost, gCost + hCost);//                neighbors.add(neighbor);//            }//        }////        return neighbors;//    }    private List<Node> getNeighbors(Node node) {        List<Node> neighbors = new ArrayList<>();        // Define the angles for possible movements        double[] angles = {0, 22.5, 45, 67.5, 90, 112.5, 135, 157.5, 180, 202.5, 225, 247.5, 270, 292.5, 315, 337.5};        // Get the current location        LngLat currentLocation = node.getLocation();        // Check if current position is within the central area        boolean currPosInCentral = lngLatHandle.isInRegion(currentLocation, centralArea);        for (double angle : angles) {            LngLat nextPos = lngLatHandle.nextPosition(currentLocation, angle);            // Check against all no-fly zones            boolean validPosition = true;            for (NamedRegion noFlyZone : noFlyZones) { // assuming noFlyZones is a List<NamedRegion>                if (lngLatHandle.isInRegion(nextPos, noFlyZone)) {                    validPosition = false;                    break;                }            }            // Add the position as a neighbor only if it's not in any no-fly zone and it's in the same region as the current position            if (validPosition && (currPosInCentral == lngLatHandle.isInRegion(nextPos, centralArea))) {                double gCost = node.getGCost() + lngLatHandle.distanceTo(currentLocation, nextPos);                double hCost = calculateHeuristic(nextPos, endNode.getLocation());                Node neighbor = new Node(node, nextPos, gCost, hCost, gCost + hCost);                neighbors.add(neighbor);            }        }        return neighbors;    }    // Define the method to get the distance between two nodes    private  double getDistance(Node node1, Node node2) {        return lngLatHandle.distanceTo(node1.getLocation(),node2.getLocation());    }//  Heuristic function for A* search. This is the Euclidean distance between the current node and the end node.    private  double calculateHeuristic(LngLat point, LngLat end) {        return lngLatHandle.distanceTo(point, end);    }}