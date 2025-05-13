package Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

// group within a classroom
public class Group {
    private Long id;
    private String title;
    private String code;
    private String classCode;    // the Classroom.code this group belongs to
    private String createdBy;    // email of the creator
    // group cal object id
    private String groupCalId;
    private List<Integer> memberIds = new ArrayList<>();

    public Group() {
    }

    public Group(Long id, String title, String code, String classCode, String createdBy) {
        this.id = id;
        this.title = title;
        this.code = code;
        this.classCode = classCode;
        this.createdBy = createdBy;
    }

    //when group is made build a group cal model and save its id here

    // Getters and setters

    public String getGroupCalId() {
        return groupCalId;
    }

    public void setGroupCalId(String groupCalId) {
        this.groupCalId = groupCalId;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }
    public String getCode() {
        return code;
    }
    public String getClassCode() {
        return classCode;
    }
    public String getCreatedBy() {
        return createdBy;
    }
    public List<Integer> getMemberIds() {return memberIds;}

    public void setId(Long id) {this.id = id;}
    public void setTitle(String title) {
        this.title = title;
    }
    public void setCode(String code) {
        this.code = code;
    }
    public void setClassCode(String classCode) {
        this.classCode = classCode;
    }
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
    public void setMemberIds(List<Integer> memberIds) {this.memberIds = memberIds;}

    public boolean addMemberId(int userId) {
        if (!memberIds.contains(userId)) {
            return memberIds.add(userId);
        }
        return false;
    }

    public boolean removeMemberId(int userId) {
        return memberIds.remove((Integer)userId);
    }}
