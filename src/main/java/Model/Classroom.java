package Model;
import java.util.ArrayList;
import java.util.List;

public class Classroom {
    private Long id;
    private String title;
    private String description;
    private String code;
    private User createdBy;
    private List<String> groupCodes = new ArrayList<>();
    private List<Integer> studentIds = new ArrayList<>();

    public Classroom(Long id, String title, String description, String code, User createdBy) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.code = code;
        this.createdBy = createdBy;
    }

    // Getters and setters
    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getCode() { return code; }
    public User getCreatedBy() { return createdBy; }
    public List<String> getGroupCodes() {return groupCodes;}
    public List<Integer> getStudentIds() {return studentIds;}

    public void setId(Long id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setCode(String code) { this.code = code; }
    public void setCreatedBy(User createdBy) { this.createdBy = createdBy; }
    public void setGroupCodes(List<String> groupCodes) {this.groupCodes = groupCodes;}
    public void setStudentIds(List<Integer> studentIds) {this.studentIds = studentIds;}

    public boolean addGroupCode(String groupCode) {
        if (!groupCodes.contains(groupCode)) {
            return groupCodes.add(groupCode);
        }
        return false;
    }

    public boolean removeGroupCode(String groupCode) {
        return groupCodes.remove(groupCode);
    }

    public boolean addStudentId(int id) {
        if (!studentIds.contains(id)) {
            return studentIds.add(id);
        }
        return false;
    }

    public boolean removeStudentId(int id) {
        return studentIds.remove((Integer) id);
    }

}
