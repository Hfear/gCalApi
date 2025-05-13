//package Controller;
//
//import ConfigAndUtil.DateAdapter;
//import Model.GroupCal;
//import Service.GroupCalService;
//import ConfigAndUtil.CalServiceBuilder;
//
//import com.google.api.services.calendar.model.Event;
//import com.google.api.services.calendar.model.EventDateTime;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import javax.servlet.http.HttpSession;
//import java.io.IOException;
//import java.security.GeneralSecurityException;
//import java.time.ZoneId;
//import java.util.List;
//import java.util.Map;
//
//@RestController
//@RequestMapping("/group/{groupCode}/calendar")
//public class GroupCalController {
//
//    private final GroupCalService groupCalService;
//
//    @Autowired
//    public GroupCalController(GroupCalService groupCalService) {
//        this.groupCalService = groupCalService;
//    }
//
//    @GetMapping("/display")
//    public List<Event> displayCalendar(
//            @PathVariable String groupCode,
//            HttpSession session
//    ) throws IOException, GeneralSecurityException {
//        return groupCalService.displayCalendar(groupCode, session);
//    }
//
//    @PostMapping("/addevent")
//    public ResponseEntity<?> addEvent(
//            @PathVariable String groupCode,
//            @RequestBody Map<String,Object> data,
//            HttpSession session
//    ) throws IOException, GeneralSecurityException {
//        Event created = groupCalService.addEvent(groupCode, data, session);
//        return ResponseEntity.ok(created);
//    }
//
//    @GetMapping("/info")
//    public GroupCal getCalendarInfo(@PathVariable String groupCode) {
//        return groupCalService.getGroupCal(groupCode);
//    }
//
//    @PostMapping("/removeevent")
//    public boolean removeEvent(
//            @PathVariable String groupCode,
//            @RequestBody Event event,
//            HttpSession session
//    ) throws IOException, GeneralSecurityException {
//        return groupCalService.removeEvent(groupCode, event, session);
//    }
//}
