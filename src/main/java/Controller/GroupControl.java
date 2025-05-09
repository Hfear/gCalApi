package Controller;

import Model.Group;
import Model.User;
import Service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;


//ROUTES FOR CLASS SPECIFIC GROUPS
@RestController
@RequestMapping("/class/{classCode}/groups")
public class GroupControl {

    @Autowired
    private GroupService groupService;

    //groups for a specific class
    @GetMapping("/listgroups")
    public List<Group> listGroups(
            @PathVariable String classCode,
            HttpSession session
    ) {
        System.out.println("Session email: " + session.getAttribute("userEmail"));
        List<Group> groups = groupService.getGroupsForClass(classCode);
        System.out.println("Loaded groups count: " + groups.size());
        return groups;
    }

    @PostMapping("/creategroup")
    public Group createGroup(
            @PathVariable String classCode,
            @RequestBody Map<String, String> body,
            HttpSession session
    ) {
        String title = body.get("title");
        System.out.println("Creating group in class: " + classCode + ", title: " + title);
        return groupService.createGroup(classCode, title, session);
    }

    @PostMapping("/joingroup")
    public boolean joinGroup(
            @PathVariable String classCode,
            @RequestBody Map<String, String> body,
            HttpSession session
    ) {
        String groupCode = body.get("groupCode");
        System.out.println("Joining group: " + groupCode + " in class: " + classCode);
        return groupService.joinGroup(groupCode, session);
    }

    @PostMapping("/leavegroup")
    public boolean leaveGroup(
            @PathVariable String classCode,
            @RequestBody Map<String, String> body,
            HttpSession session
    ) {
        String groupCode = body.get("groupCode");
        System.out.println("Leaving group: " + groupCode + " in class: " + classCode);
        return groupService.leaveGroup(groupCode, session);
    }

    @GetMapping("/{groupCode}/members")
    public List<User> listMembers(
            @PathVariable String classCode,
            @PathVariable String groupCode,
            HttpSession session
    ) {
        System.out.println("Listing members of group: " + groupCode);
        return groupService.getGroupMembers(groupCode);
    }

    @GetMapping("/{groupCode}/details")
    public Map<String, Object> getGroupDetails(
            @PathVariable String classCode,
            @PathVariable String groupCode
    ) {
        return groupService.getGroupDetails(groupCode);
    }


}
