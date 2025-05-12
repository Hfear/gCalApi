package Database;

import Model.Classroom;
import Model.User;
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

public class ClassroomDatabase {
    private static final ConcurrentHashMap<String, Classroom> classroomMap = new ConcurrentHashMap<>();
    private static final File classroomDbFile = new File("src/main/resources/classroomdb.json");
    private static long idCounter = 1;

    public static void fillHashMap() {
        try (InputStream in = ClassroomDatabase.class.getResourceAsStream("/classroomdb.json")) {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(in);
            JsonNode classrooms = root.get("classrooms");

            for (JsonNode cNode : classrooms) {
                String creatorEmail = cNode.get("createdBy").asText();
                User creator = UserDatabase.getUser(creatorEmail);

                long id = cNode.get("id").asLong();
                Classroom classroom = new Classroom(
                        id,
                        cNode.get("title").asText(),
                        cNode.get("description").asText(),
                        cNode.get("code").asText(),
                        creator
                );
                idCounter = Math.max(idCounter, id + 1);

                //loading userids into array
                if (cNode.has("studentIds")) {
                    for (JsonNode idNode : cNode.get("studentIds")) {
                        classroom.addStudentId(idNode.asInt());
                    }
                }

                // load groupCodes
                if (cNode.has("groupCodes")) {
                    for (JsonNode gNode : cNode.get("groupCodes")) {
                        classroom.addGroupCode(gNode.asText());
                    }
                }

                classroomMap.put(classroom.getCode(), classroom);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to load classroomdb.json", e);
        }
    }

    public static Collection<Classroom> getAllClassrooms() {
        return classroomMap.values();
    }

    public static Classroom getClassroomByCode(String code) {
        return classroomMap.get(code);
    }

    public static void addClassroom(Classroom classroom) {
        if (classroom.getId() == null) {
            classroom.setId(idCounter++);
        }
        classroomMap.put(classroom.getCode(), classroom);
        persistAll();
    }


    public static boolean classroomExists(String code) {
        return classroomMap.containsKey(code);
    }

   //persisting back to db
    public static void persistAll() {
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode root = mapper.createObjectNode();
        ArrayNode arr = mapper.createArrayNode();

        for (Classroom c : classroomMap.values()) {
            ObjectNode obj = mapper.createObjectNode();
            obj.put("id", c.getId());
            obj.put("title", c.getTitle());
            obj.put("description", c.getDescription());
            obj.put("code", c.getCode());

            String creatorEmail = c.getCreatedBy() != null ? c.getCreatedBy().getEmail() : "unknown@example.com";
            obj.put("createdBy", creatorEmail);


            ArrayNode idsArr = mapper.createArrayNode();
            for (Integer id : c.getStudentIds()) {
                idsArr.add(id);
            }
            obj.set("studentIds", idsArr);

            ArrayNode groups = mapper.createArrayNode();
            for (String gcode : c.getGroupCodes()) {
                groups.add(gcode);
            }
            obj.set("groupCodes", groups);

            arr.add(obj);
        }
        root.set("classrooms", arr);

        try {
            mapper.writerWithDefaultPrettyPrinter().writeValue(classroomDbFile, root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void clear() {
        classroomMap.clear();
    }

}
