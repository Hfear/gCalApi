package Controller;

import Model.GroupCal;
import Service.GroupCalService;
import Service.ICalendarService;

import com.google.api.services.calendar.model.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/calendar")
public class CalendarController {

    @Autowired
    @Qualifier("groupCalService")
    private ICalendarService groupCalService;

    @Autowired
    @Qualifier("userCalendarService")
    private ICalendarService userCalService;

    //  GROUP CALENDAR ROUTES

    @GetMapping("/group/{groupCode}/display")
    public List<Event> displayGroupCalendar(
            @PathVariable String groupCode,
            HttpSession session
    ) throws IOException, GeneralSecurityException {
        return groupCalService.displayCalendar(groupCode, session);
    }

    @PostMapping("/group/{groupCode}/addevent")
    public ResponseEntity<?> addGroupEvent(
            @PathVariable String groupCode,
            @RequestBody Map<String, Object> data,
            HttpSession session
    ) throws IOException, GeneralSecurityException {
        Event created = groupCalService.addEvent(groupCode, data, session);
        return ResponseEntity.ok(created);
    }

    @PostMapping("/group/{groupCode}/removeevent")
    public boolean removeGroupEvent(
            @PathVariable String groupCode,
            @RequestBody Event event,
            HttpSession session
    ) throws IOException, GeneralSecurityException {
        return groupCalService.removeEvent(groupCode, event, session);
    }

    @GetMapping("/group/{groupCode}/info")
    public GroupCal getCalendarInfo(@PathVariable String groupCode) {
        return ((GroupCalService) groupCalService).getGroupCal(groupCode);
    }



    // USER CALENDAR ROUTES

    @GetMapping("/user/display")
    public List<Event> displayUserCalendar(HttpSession session) throws IOException, GeneralSecurityException {
        return userCalService.displayCalendar("primary", session);
    }

    @PostMapping("/user/addevent")
    public ResponseEntity<?> addUserEvent(
            @RequestBody Map<String, Object> data,
            HttpSession session
    ) throws IOException, GeneralSecurityException {
        Event created = userCalService.addEvent("primary", data, session);
        return ResponseEntity.ok(created);
    }

    @PostMapping("/user/removeevent")
    public boolean removeUserEvent(
            @RequestBody Event event,
            HttpSession session
    ) throws IOException, GeneralSecurityException {
        return userCalService.removeEvent("primary", event, session);
    }
}
