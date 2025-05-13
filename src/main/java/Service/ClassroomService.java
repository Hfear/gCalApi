package Service;

import Model.Classroom;
import Model.Group;
import Model.User;
import Database.UserDatabase;
import Database.ClassroomDatabase;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.UUID;


@Service
public class ClassroomService {

    public List<Classroom> getUsersClasses(String email) {
        User user = UserDatabase.getUser(email);

        List<Classroom> classList = new ArrayList<>();

        if (user == null) return classList;

        for (String code : user.getEnrolledClassCodes()) {
            Classroom classroom = ClassroomDatabase.getClassroomByCode(code);
            if (classroom != null) {
                classList.add(classroom);
            }
        }
        return classList;
    }

    public Classroom getClassroomByCode(String code) {
        return ClassroomDatabase.getClassroomByCode(code);
    }

    public List<Integer> getStudentIdsForClass(String classCode) {
        Classroom curClass = ClassroomDatabase.getClassroomByCode(classCode);
        if (curClass == null) return Collections.emptyList();
        return curClass.getStudentIds();
    }

    public Classroom createClass(String title, HttpSession session) {
        String creatorEmail = (String) session.getAttribute("userEmail");
        if (creatorEmail == null) {
            throw new RuntimeException("User not authenticated");
        }

        User creator = UserDatabase.getUser(creatorEmail);
        if (creator == null) {
            throw new RuntimeException("User not found");
        }

        String code = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        Classroom newClass = new Classroom(null, title, "", code, creator);

        ClassroomDatabase.addClassroom(newClass);

        // enroll the creator automatically
        creator.enroll(code);
        newClass.addStudentId(creator.getUserId());
        UserDatabase.persistUser(creator);
        ClassroomDatabase.persistAll();

        return newClass;
    }

    public void remClass(Classroom classroom) {}
    public void addGroup(Group group) {}
    public void remGroup(Group group) {}

}
