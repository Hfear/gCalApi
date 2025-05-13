import Model.Group;
import Model.User;
import Database.GroupDatabase;
import Database.UserDatabase;
import Service.GroupService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class GroupServiceTest {

    private GroupService service;
    private HttpSession session;

    @BeforeEach
    void setup() {
        service = new GroupService();
        session = mock(HttpSession.class);
    }

    @Test
    void testCreateGroup_validSession_createsGroup() {
        User user = new User(1, "Dany", "dany@example.com");
        UserDatabase.postUser(user);

        when(session.getAttribute("userEmail")).thenReturn("dany@example.com");

        Group group = service.createGroup("CSC101", "Final Project", session);
        assertNotNull(group);
        assertEquals("Final Project", group.getTitle());
        assertEquals("CSC101", group.getClassCode());
        assertEquals("dany@example.com", group.getCreatedBy());
    }

    @Test
    void testJoinGroup_groupExists_addsUser() {
        User user = new User(2, "Leo", "leo@example.com");
        UserDatabase.postUser(user);

        Group group = new Group(null, "AI Group", "JOIN01", "CSC202", "someone@example.com");
        GroupDatabase.addGroup(group);

        when(session.getAttribute("userEmail")).thenReturn("leo@example.com");

        boolean joined = service.joinGroup("JOIN01", session);
        assertTrue(joined);

        Group updated = GroupDatabase.getByCode("JOIN01");
        assertTrue(updated.getMemberIds().contains(user.getUserId()));
    }

    @Test
    void testLeaveGroup_groupExists_removesUser() {
        User user = new User(11, "Sam", "sam@example.com");
        UserDatabase.postUser(user);

        Group group = new Group(null, "Solo Group", "UNIQUE01", "SOLO101", "creator@example.com");
        group.addMemberId(user.getUserId());
        GroupDatabase.addGroup(group);

        when(session.getAttribute("userEmail")).thenReturn("sam@example.com");

        boolean result = service.leaveGroup("UNIQUE01", session);
        assertTrue(result);

        Group updated = GroupDatabase.getByCode("UNIQUE01");
        assertNotNull(updated);
        assertFalse(updated.getMemberIds().contains(user.getUserId()));
    }

    @Test
    void testGetGroupMembers_returnsUserList() {
        User user = new User(12, "Mila", "mila@example.com");
        UserDatabase.postUser(user);

        Group group = new Group(null, "Science Team", "MEMFIX", "SCI101", "admin@example.com");
        group.addMemberId(user.getUserId());
        GroupDatabase.addGroup(group);

        List<User> members = service.getGroupMembers("MEMFIX");
        assertEquals(1, members.size());
        assertEquals("mila@example.com", members.get(0).getEmail());
    }

    @Test
    void testGetGroupDetails_validGroup_returnsCorrectMap() {
        User user = new User(10, "Eva", "eva@example.com");
        UserDatabase.postUser(user);

        Group group = new Group(null, "TestGroup", "GRP01", "CSC999", "eva@example.com");
        group.addMemberId(user.getUserId());
        GroupDatabase.addGroup(group);

        Map<String, Object> details = service.getGroupDetails("GRP01");

        assertEquals("TestGroup", details.get("title"));
        assertTrue(((List<?>) details.get("memberNames")).contains("Eva"));
        assertEquals("eva@example.com", details.get("createdBy"));
    }

    @Test
    void testGetGroupDetails_invalidGroup_throwsException() {
        assertThrows(RuntimeException.class, () -> {
            service.getGroupDetails("INVALID_CODE");
        });
    }
}
