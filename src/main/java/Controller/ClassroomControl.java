package Controller;

import Service.ClassroomService;
import Service.UserService;
import Model.Classroom;
import Model.User;
import Database.UserDatabase;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.*;

@RestController
public class ClassroomControl {

    @Autowired
    private ClassroomService classService;

    @Autowired
    private UserService userService;

    @GetMapping("/my-classes")
    public List<Classroom> getUserClasses(HttpSession session) {
        User user = userService.getCurrentUser(session);
        if (user == null) {
            System.out.println("No user in session.");
            return null;
        }

        System.out.println("Loaded user: " + user.getName() + " (" + user.getEmail() + ")");
        System.out.println("Enrolled class codes: " + user.getEnrolledClassCodes());

        return classService.getUsersClasses(user.getEmail());
    }

    @PostMapping("/join")
    public boolean joinClass(HttpSession session, @RequestBody Map<String, String> body) {
        String code = body.get("code");

        boolean enrolled = userService.enrollInClassroom(session, code);
        System.out.println("Enrollment success: " + enrolled);

        return enrolled;
    }

    @GetMapping("/class/{code}")
    public Classroom getClassByCode(@PathVariable String code) {
        return classService.getClassroomByCode(code);
    }

    @PostMapping("/class/student-ids")
    public List<Integer> getClassStudentIds(@RequestBody Map<String,String> body) {
        String code = body.get("code");
        return classService.getStudentIdsForClass(code);
    }

    @PostMapping("/class/create")
    public Classroom createClass(@RequestBody Map<String, String> body, HttpSession session) {
        String title = body.get("title");
        return classService.createClass(title, session);
    }


    @GetMapping("/user/{id}")
    public User getUserById(@PathVariable int id) {
        return UserDatabase.getUserById(id);
    }

}
