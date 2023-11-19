package uk.ac.ed.inf.Controller;import com.fasterxml.jackson.databind.ObjectMapper;import com.fasterxml.jackson.databind.SerializationFeature;import com.fasterxml.jackson.databind.module.SimpleModule;import org.json.JSONArray;import org.json.JSONObject;import uk.ac.ed.inf.Model.DroneMove;import uk.ac.ed.inf.Serialiser.CustomOrderSerializer;import uk.ac.ed.inf.ilp.data.Order;import java.io.IOException;import java.nio.file.Files;import java.nio.file.Path;import java.nio.file.Paths;import java.nio.file.StandardOpenOption;import java.time.LocalDate;import java.time.format.DateTimeFormatter;import java.util.List;import java.util.stream.Collectors;/** * Class providing methods to write data structures to the corresponding json or geo-json files */public class DroneDeliveryRecorder {    private LocalDate date;    private Path resultFilesDirectory;    private ObjectMapper objectMapper;    public DroneDeliveryRecorder(LocalDate date) {        this.date = date;        this.resultFilesDirectory = Paths.get("ResultFiles");        this.objectMapper = new ObjectMapper();        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);        try {            Files.createDirectories(resultFilesDirectory); // Ensure the directory exists        } catch (IOException e) {            e.printStackTrace();        }    }    /**     * Generates a JSON file containing a list of delivery orders.     *     * @param orders The list of Order objects to be serialized into JSON.     */    public void createDeliveriesFile(List<Order> orders) {        String filename = "deliveries-" + date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".json";        Path filePath = resultFilesDirectory.resolve(filename);        SimpleModule module = new SimpleModule();        module.addSerializer(Order.class, new CustomOrderSerializer());        objectMapper.registerModule(module);        try {            String jsonContent = objectMapper.writeValueAsString(orders);            Files.writeString(filePath, jsonContent, StandardOpenOption.CREATE);            System.out.println("Deliveries file created successfully");        } catch (IOException e) {            e.printStackTrace();        }    }    /**     * Generates a JSON file containing the flight path of drones.     * The flight paths are aggregated from a list of DroneMove objects and saved as JSON.     * The file is named using the date and saved in the specified directory.     *     * @param droneMovesLists A list of lists containing DroneMove objects for each flight path.     */    public void createFlightPathFile(List<List<DroneMove>> droneMovesLists) {        List<DroneMove> allMoves = droneMovesLists.stream()                .flatMap(List::stream)                .collect(Collectors.toList());        String filename = "flightpath-" + date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".json";        Path filePath = resultFilesDirectory.resolve(filename);        this.objectMapper = new ObjectMapper();        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);        try {            String jsonContent = objectMapper.writeValueAsString(allMoves);            Files.writeString(filePath, jsonContent, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);            System.out.println("Flight path JSON file created successfully");        } catch (IOException e) {            e.printStackTrace();        }    }    /**     * Generates a GeoJSON file representing the flight paths of drones.     * Each flight path is constructed from a list of DroneMove objects and saved as a GeoJSON feature.     * The file is named using the date and saved in the specified directory.     *     * @param droneMovesLists A list of lists containing DroneMove objects for each flight path.     */    public void createGeoJsonFile(List<List<DroneMove>> droneMovesLists) {        String filename = "drone-" + date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + ".geojson";        Path filePath = resultFilesDirectory.resolve(filename);        // Create the GeoJSON structure        JSONObject geoJson = new JSONObject();        geoJson.put("type", "FeatureCollection");        JSONArray features = new JSONArray();        JSONObject feature = new JSONObject();        feature.put("type", "Feature");        feature.put("properties", new JSONObject()); // Empty properties object        JSONObject geometry = new JSONObject();        geometry.put("type", "LineString");        JSONArray coordinates = new JSONArray();        // Add coordinates from all DroneMove objects        for (List<DroneMove> droneMoves : droneMovesLists) {            for (DroneMove move : droneMoves) {                JSONArray startCoord = new JSONArray();                startCoord.put(move.getFromLongitude());                startCoord.put(move.getFromLatitude());                coordinates.put(startCoord);                JSONArray endCoord = new JSONArray();                endCoord.put(move.getToLongitude());                endCoord.put(move.getToLatitude());                coordinates.put(endCoord);            }        }        geometry.put("coordinates", coordinates);        feature.put("geometry", geometry);        features.put(feature);        geoJson.put("features", features);        // Write to file        try {            Files.writeString(filePath, geoJson.toString(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);            System.out.println("GeoJSON file created successfully");        } catch (IOException e) {            e.printStackTrace();        }    }}