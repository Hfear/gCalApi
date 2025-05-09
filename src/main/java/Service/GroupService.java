package Service;

import Database.GroupDatabase;
import Database.UserDatabase;
import Service.GroupCalService;
import Model.Group;
import Model.User;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class GroupService {


    public List<Group> getGroupsForClass(String classCode) {
        return GroupDatabase.getGroupsForClass(classCode);
    }

    public Group createGroup(String classCode, String title, HttpSession session) {
        String userEmail = (String) session.getAttribute("userEmail");
        if (userEmail == null) {
            throw new RuntimeException("Not authenticated");
        }
        String groupCode = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        Group group = new Group(null, title, groupCode, classCode, userEmail);

        GroupDatabase.addGroup(group);
        return group;
    }

    public boolean joinGroup(String groupCode, HttpSession session) {
        String userEmail = (String) session.getAttribute("userEmail");
        User user = UserDatabase.getUser(userEmail);
        if (user == null) return false;

        Group group = GroupDatabase.getByCode(groupCode);
        if (group == null) return false;

        boolean added = group.addMemberId(user.getUserId());
        if (added) {
            GroupDatabase.persistGroup(group);
        }
        return added;
    }

    public boolean leaveGroup(String groupCode, HttpSession session) {
        String userEmail = (String) session.getAttribute("userEmail");
        User user = UserDatabase.getUser(userEmail);
        if (user == null) return false;

        Group group = GroupDatabase.getByCode(groupCode);
        if (group == null) return false;

        boolean removed = group.removeMemberId(user.getUserId());
        if (removed) {
            GroupDatabase.persistGroup(group);
        }
        return removed;
    }

    public List<User> getGroupMembers(String groupCode) {
        Group group = GroupDatabase.getByCode(groupCode);
        if (group == null) return List.of(); //empty list

        return group.getMemberIds().stream()
                .map(UserDatabase::getUserById)
                .filter(u -> u != null)
                .collect(Collectors.toList());
    }


    // eventually add group calendar and maybe notes?
    public Map<String, Object> getGroupDetails(String groupCode) {
        Group group = GroupDatabase.getByCode(groupCode);
        if (group == null) throw new RuntimeException("Group not found");

        List<String> memberNames = new ArrayList<>();
        for (int id : group.getMemberIds()) {
            User u = UserDatabase.getUserById(id);
            if (u != null) memberNames.add(u.getName());
        }

        Map<String, Object> groupDetails = new HashMap<>();
        groupDetails.put("title", group.getTitle());
        groupDetails.put("memberNames", memberNames);
        groupDetails.put("code", group.getCode());
        groupDetails.put("createdBy", group.getCreatedBy());

        return groupDetails;
    }

    public List<Group> getGroupsForCurrentUser(HttpSession session) {
        String userEmail = (String) session.getAttribute("userEmail");
        User user = UserDatabase.getUser(userEmail);
        if (user == null) return List.of();

        int userId = user.getUserId();
        return GroupDatabase.getAllGroups().stream()
                .filter(g -> g.getMemberIds().contains(userId))
                .collect(Collectors.toList());
    }




}
