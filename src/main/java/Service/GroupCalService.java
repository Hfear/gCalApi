package Service;

import ConfigAndUtil.CalServiceBuilder;
import Model.GroupCal;
import Model.User;
import Model.Group;
import Database.GroupDatabase;
import ConfigAndUtil.DateAdapter;
import com.google.api.services.calendar.model.EventDateTime;
import Database.GroupCalDatabase;

import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.AclRule;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.logging.Logger;
import java.time.ZoneId;
import java.util.Map;


@Service("groupCalService")
public class GroupCalService implements ICalendarService {

    @Autowired
    private GroupService groupService;

    private static final Logger logger = Logger.getLogger(GroupCalService.class.getName());

    // gets group cal object
    // build cal and saves in groupcal model
    public String buildGroupCal(String groupCode, String groupTitle, String ownerEmail) throws IOException, GeneralSecurityException {
        Calendar service = CalServiceBuilder.buildService(ownerEmail);

        com.google.api.services.calendar.model.Calendar calendar = new com.google.api.services.calendar.model.Calendar();
        calendar.setSummary(groupTitle + " Calendar");

        com.google.api.services.calendar.model.Calendar created = service.calendars().insert(calendar).execute();

        // making the group cal fully public cause only public cals can b inbedded
        AclRule rule = new AclRule();
        AclRule.Scope scope = new AclRule.Scope();
        scope.setType("default");
        rule.setScope(scope);
        rule.setRole("reader");

        service.acl().insert(created.getId(), rule).execute();

        // save to db
        GroupCal newCal = new GroupCal(null, groupCode, created.getId(), ownerEmail);
        GroupCalDatabase.addGroupCal(newCal);

        return created.getId();
    }

    public GroupCal getGroupCal(String groupCode) {
        GroupCal groupCal = GroupCalDatabase.getByGroupCode(groupCode);
        if (groupCal == null || groupCal.getCalendarId() == null) {
            throw new RuntimeException("Group or calendar not found for group: " + groupCode);
        }
        return groupCal;
    }


    public List<Event> getEvents(String groupCode, HttpSession session) throws IOException, GeneralSecurityException {
        String userEmail = (String) session.getAttribute("userEmail");
        if (userEmail == null) throw new RuntimeException("Not authenticated");

        GroupCal groupCal = GroupCalDatabase.getByGroupCode(groupCode);
        if (groupCal == null) throw new RuntimeException("Group or calendar not found for group: " + groupCode);

        Calendar service = CalServiceBuilder.buildService(userEmail);
        Events response = service.events().list(groupCal.getCalendarId())
                .setMaxResults(10)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();

        return response.getItems();
    }

    public List<Event> displayCalendar(String groupCode, HttpSession session) throws IOException, GeneralSecurityException {
        String userEmail = (String) session.getAttribute("userEmail");
        if (userEmail == null) throw new RuntimeException("Not authenticated");

        GroupCal groupCal = this.getGroupCal(groupCode);
        Calendar service = CalServiceBuilder.buildService(userEmail);

        Events events = service.events().list(groupCal.getCalendarId())
                .setMaxResults(10)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();

        return events.getItems();
    }


    public Event addEvent(String groupCode, Map<String,Object> data, HttpSession session
    ) throws IOException, GeneralSecurityException {
        //  get group calendar info
        GroupCal gc = getGroupCal(groupCode);
        String ownerEmail = gc.getOwnerEmail();
        String calendarId = gc.getCalendarId();

        //  build cal w client as owner
        com.google.api.services.calendar.Calendar service =
                CalServiceBuilder.buildService(ownerEmail);

        // extract and adapt payload
        String summary     = (String) data.get("summary");
        String location    = (String) data.get("location");
        String description = (String) data.get("description");
        String startRaw    = ((Map<?,?>)data.get("start")).get("dateTime").toString();
        String endRaw      = ((Map<?,?>)data.get("end")).get("dateTime").toString();

        EventDateTime start = DateAdapter.parseEventDateTime(startRaw, ZoneId.of("America/New_York"));
        EventDateTime end   = DateAdapter.parseEventDateTime(endRaw,   ZoneId.of("America/New_York"));

        Event e = new Event()
                .setSummary(summary)
                .setLocation(location)
                .setDescription(description)
                .setStart(start)
                .setEnd(end);

        // 4) insert into the group’s calendar
        return service.events()
                .insert(calendarId, e)
                .execute();
    }


    public boolean removeEvent(String groupCode, Event event, HttpSession session) throws IOException, GeneralSecurityException {
        String userEmail = (String) session.getAttribute("userEmail");
        if (userEmail == null) throw new RuntimeException("Not authenticated");

        GroupCal groupCal = getGroupCal(groupCode);
        Calendar service = CalServiceBuilder.buildService(userEmail);

        try {
            service.events().delete(groupCal.getCalendarId(), event.getId()).execute();
            return true;
        } catch (IOException e) {
            logger.severe("Error removing event: " + e.getMessage());
            return false;
        }
    }



}
