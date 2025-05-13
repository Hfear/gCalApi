package Database;

import Model.GroupCal;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public class GroupCalDatabase {
    private static final ConcurrentHashMap<String, GroupCal> groupCalMap = new ConcurrentHashMap<>();
    private static final File dbFile = new File("src/main/resources/groupcaldb.json");
    private static long idCounter = 1;

    public static void fillHashMap() {
        try (InputStream in = GroupCalDatabase.class.getResourceAsStream("/groupcaldb.json")) {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(in);
            JsonNode array = root.get("groupCals");

            if (array != null && array.isArray()) {
                for (JsonNode n : array) {
                    GroupCal gc = new GroupCal(
                            n.get("id").asLong(),
                            n.get("groupCode").asText(),
                            n.get("calendarId").asText(),
                            n.get("ownerEmail").asText()
                    );
                    groupCalMap.put(gc.getGroupCode(), gc);
                    idCounter = Math.max(idCounter, gc.getId() + 1);
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to load groupcaldb.json", e);
        }
    }

    public static void addGroupCal(GroupCal cal) {
        if (cal.getId() == null) {
            cal.setId(idCounter++);
        }
        groupCalMap.put(cal.getGroupCode(), cal);
        persistAll();
    }

    public static GroupCal getByGroupCode(String groupCode) {
        return groupCalMap.get(groupCode);
    }

    public static Collection<GroupCal> getAll() {
        return groupCalMap.values();
    }

    public static void persistAll() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode root = mapper.createObjectNode();
        ArrayNode arr = mapper.createArrayNode();

        for (GroupCal gc : groupCalMap.values()) {
            ObjectNode n = mapper.createObjectNode();
            n.put("id", gc.getId());
            n.put("groupCode", gc.getGroupCode());
            n.put("calendarId", gc.getCalendarId());
            n.put("ownerEmail", gc.getOwnerEmail());
            arr.add(n);
        }

        root.set("groupCals", arr);
        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(dbFile, root);
            System.out.println("groupcaldb.json updated.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}