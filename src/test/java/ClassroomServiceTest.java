import Model.Classroom;
import Model.User;
import Database.UserDatabase;
import Database.ClassroomDatabase;
import Service.ClassroomService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ClassroomServiceTest {

    @BeforeEach
    void setup() {
        UserDatabase.clear();
        ClassroomDatabase.clear();
    }

    @Test
    void testGetUsersClasses_withRealStaticUserDatabase() {
        User user = new User(99, "Test", "test@example.com");
        user.enroll("Intro");
        UserDatabase.postUser(user);

        Classroom classroom = new Classroom(1L, "Math", "MATH101", "Intro", user);
        classroom.addStudentId(user.getUserId());
        ClassroomDatabase.addClassroom(classroom);

        List<Classroom> result = new ClassroomService().getUsersClasses("test@example.com");

        System.out.println("Returned classes: " + result.size());
        assertEquals(1, result.size());
        assertEquals("Math", result.get(0).getTitle());
    }

    @Test
    void testGetStudentIdsForClass_validClass_returnsIds() {
        // Create and save user
        User user = new User(1, "TestUser", "testuser@example.com");
        user.setUserId(1);
        user.enroll("Intro");
        UserDatabase.postUser(user);
        UserDatabase.persistUser(user);

        Classroom classroom = new Classroom(1L, "Math", "MATH101", "Intro", user);
        classroom.addStudentId(user.getUserId());
        ClassroomDatabase.addClassroom(classroom);
        ClassroomDatabase.persistAll();

        List<Integer> ids = new ClassroomService().getStudentIdsForClass("Intro");
        System.out.println("Student IDs: " + ids);

        assertEquals(1, ids.size());
        assertEquals(0, ids.get(0));
    }




    @Test
    void testGetStudentIdsForClass_nonexistentClass_returnsEmptyList() {
        List<Integer> ids = new ClassroomService().getStudentIdsForClass("NOT_REAL");
        assertTrue(ids.isEmpty());
    }
}
