package Database;

import Model.Group;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.UUID;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class GroupDatabase {
    private static final ConcurrentHashMap<String, Group> groupMap = new ConcurrentHashMap<>();
    private static final File groupDbFile = new File("src/main/resources/groupdb.json");
    private static long idIndex = 0;

    public static void fillHashMap() {
        try (InputStream in = GroupDatabase.class.getResourceAsStream("/groupdb.json")) {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode arr = mapper.readTree(in).get("groups");

            for (JsonNode n : arr) {
                try {
                    // in a normal database this would be where the NOT NULL constraints and field enforcements
                    long id = n.has("id") ? n.get("id").asLong() : -1;
                    String title = n.has("title") ? n.get("title").asText() : "(Untitled)";
                    String code = n.has("code") ? n.get("code").asText() : UUID.randomUUID().toString().substring(0, 6).toUpperCase();
                    String classCode = n.has("classCode") ? n.get("classCode").asText() : "UNKNOWN";
                    String createdBy = n.has("createdBy") ? n.get("createdBy").asText() : "unknown@example.com";

                    Group g = new Group(id, title, code, classCode, createdBy);

                    // group members
                    if (n.has("memberIds") && n.get("memberIds").isArray()) {
                        for (JsonNode m : n.get("memberIds")) {
                            g.addMemberId(m.asInt());
                        }
                    }
                    if (n.has("groupCalId")) {
                        g.setGroupCalId(n.get("groupCalId").asText());
                    }

                    groupMap.put(g.getCode(), g);
                    idIndex = Math.max(idIndex, g.getId() + 1);
                } catch (Exception e) {
                    System.err.println("Failed to load group entry: " + n);
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to load groupdb.json", e);
        }
    }

    public static List<Group> getGroupsForClass(String classCode) {
        return groupMap.values().stream()
                .filter(g -> classCode.equals(g.getClassCode()))
                .collect(Collectors.toList());
    }

    public static Group getByCode(String code) {
        return groupMap.get(code);
    }

    public static void addGroup(Group group) {
        if (group.getId() == null) {
            group.setId(idIndex++);
        }
        groupMap.put(group.getCode(), group);
        persistGroup(group);
    }

    public static Collection<Group> getAllGroups() {
        return groupMap.values();
    }


    public static void persistGroup(Group g) {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode root = mapper.createObjectNode();
        ArrayNode arr  = mapper.createArrayNode();

        for (Group group : groupMap.values()) {
            ObjectNode gNode = mapper.createObjectNode();
            gNode.put("id", group.getId());
            gNode.put("title", group.getTitle());
            gNode.put("code", group.getCode());
            gNode.put("classCode", group.getClassCode());
            gNode.put("createdBy", group.getCreatedBy());
            gNode.put("groupCalId", group.getGroupCalId());

            // write out memberIds
            ArrayNode mems = mapper.createArrayNode();
            for (Integer uid : group.getMemberIds()) {
                mems.add(uid);
            }
            gNode.set("memberIds", mems);
            arr.add(gNode);
        }
        root.set("groups", arr);

        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(groupDbFile, root);
            System.out.println("groupdb.json updated.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
