package Service;

import Model.User;
import Model.Classroom;
import org.springframework.stereotype.Service;
import Database.UserDatabase;
import Database.ClassroomDatabase;
import javax.servlet.http.HttpSession;


// sign up login etc logic
@Service
public class UserService {

    public void login() {}
    public void logout() {}

    public User getCurrentUser(HttpSession session) {
        String email = (String) session.getAttribute("userEmail");
        if (email == null) return null;
        return UserDatabase.getUser(email);
    }

    public Boolean enrollInClassroom(HttpSession session, String classCode) {
        Classroom classroom = ClassroomDatabase.getClassroomByCode(classCode);
        if (classroom == null) {
            System.out.println("Classroom not found");
            return false;
        }

        User user = getCurrentUser(session);
        if (user == null) {
            System.out.println("User not found");
            return false;
        }

        boolean enrolled = user.enroll(classCode);
        if (enrolled) {
            classroom.addStudentId(user.getUserId());
            ClassroomDatabase.persistAll();
            UserDatabase.persistUser(user);
        } else {
            System.out.println("Already enrolled");
        }
        return enrolled;
    }

    public boolean leaveClassroom(HttpSession session, String classCode) {
        Classroom classroom = ClassroomDatabase.getClassroomByCode(classCode);
        if (classroom == null) {
            System.out.println("Classroom not found");
            return false;
        }

        User user = getCurrentUser(session);
        if (user == null) {
            System.out.println("User not found");
            return false;
        }

        boolean removed = user.leaveClass(classCode);
        if (removed) {
            classroom.removeStudentId(user.getUserId());
            ClassroomDatabase.persistAll();
            UserDatabase.persistUser(user);
            System.out.println("Classroom removed");
        } else {
            System.out.println("User was not enrolled in classroom");
        }

        return removed;
    }

}