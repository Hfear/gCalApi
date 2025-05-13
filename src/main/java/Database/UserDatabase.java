package Database;

import Model.User;
import Model.Classroom;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Objects;
import java.util.List;
import java.util.stream.Collectors;
import java.io.File;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ArrayNode;



//fake database using HashMap and json persistance until we add a real one
public class UserDatabase {

    private static final ConcurrentHashMap<String, User> userMap = new ConcurrentHashMap<>();
    private static int idIndex = 0;
    private static final File userDbFile = new File("src/main/resources/userdb.json");

    public static void fillHashMap() {
        try (InputStream in = UserDatabase.class.getResourceAsStream("/userdb.json")) {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode users = mapper.readTree(in).get("users");

            for (JsonNode uNode : users) {
                try {
                    int userId = uNode.has("userId") ? uNode.get("userId").asInt() : -1;
                    String name = uNode.has("name") ? uNode.get("name").asText() : "Unknown";
                    String email = uNode.has("email") ? uNode.get("email").asText() : "unknown@example.com";

                    User user = new User(userId, name, email);

                    if (uNode.has("enrolledClassCodes") && uNode.get("enrolledClassCodes").isArray()) {
                        for (JsonNode codeNode : uNode.get("enrolledClassCodes")) {
                            user.enroll(codeNode.asText());
                        }
                    }

                    userMap.put(email, user);
                    idIndex = Math.max(idIndex, userId + 1);

                } catch (Exception e) {
                    System.err.println(" Failed to load user: " + uNode);
                    e.printStackTrace();
                }
            }

        } catch (IOException e) {
            throw new UncheckedIOException("Failed to load userdb.json", e);
        }
    }



    public static void postUser(User user) {
        if (!userMap.containsKey(user.getEmail())) {
            user.setUserId(idIndex++);
        }
        userMap.put(user.getEmail(), user);
    }

    public static User getUser(String email) {

        return userMap.get(email);
    }

    public static User getUserById(int id) {
        return userMap.values().stream()
                .filter(u -> u.getUserId() == id)
                .findFirst()
                .orElse(null);
    }

    public static void persistUser(User user) {
        userMap.put(user.getEmail(), user);
        saveToJsonFile();
    }

    private static void saveToJsonFile() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode root = mapper.createObjectNode();
        ArrayNode usersArray = mapper.createArrayNode();

        for (User user : userMap.values()) {
            ObjectNode userNode = mapper.createObjectNode();
            userNode.put("userId", user.getUserId());
            userNode.put("name", user.getName());
            userNode.put("email", user.getEmail());

            ArrayNode classCodes = mapper.createArrayNode();
            for (String code : user.getEnrolledClassCodes()) {
                classCodes.add(code);
            }
            userNode.set("enrolledClassCodes", classCodes);
            usersArray.add(userNode);
        }
        root.set("users", usersArray);

        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(userDbFile, root);
            System.out.println("userdb.json updated.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void clear() {
        userMap.clear();
    }
}
