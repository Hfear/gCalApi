package Model;

import java.util.ArrayList;
import java.util.List;

public class User {

    protected int userId;
    protected String name;
    protected String email;
    private List<String> enrolledClassCodes = new ArrayList<>();

    public User(int userId, String name, String email) {
        this.userId = userId;
        this.name = name;
        this.email = email;
    }

    // getters n setters
    public int getUserId() { return userId; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public List<String> getEnrolledClassCodes() {return enrolledClassCodes;}

    public User setEmail(String email) {this.email = email;return this;}
    public User setName(String name) {this.name = name;return this;}
    public User setUserId(int userId) {this.userId = userId;return this;}

    public boolean enroll(String classroom) {
        if (!enrolledClassCodes.contains(classroom)) {
            return enrolledClassCodes.add(classroom);
        }
        return false;
    }

    public boolean leaveClass(String classCode) {
        return enrolledClassCodes.remove(classCode);
    }


}
