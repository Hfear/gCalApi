package Controller;

import Database.UserDatabase;
import Service.GroupService;
import Model.User;
import Model.Group;
import java.util.List;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserControl {

    @Autowired
    private GroupService groupService;

    @GetMapping("/profile")
    public User getCurrentUser(HttpSession session) {

        String email = (String) session.getAttribute("userEmail");
        System.out.println("profile call has Session email: " + email);

        return UserDatabase.getUser(email);
    }

    @GetMapping("/my-groups")
    public List<Group> getMyGroups(HttpSession session) {
        return groupService.getGroupsForCurrentUser(session);
    }


}

