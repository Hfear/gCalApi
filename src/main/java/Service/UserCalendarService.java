package Service;

import ConfigAndUtil.CalServiceBuilder;
import ConfigAndUtil.DateAdapter;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

@Service("userCalendarService") //  for @Qualifier
public class UserCalendarService implements ICalendarService {

    @Override
    public List<Event> getEvents(String calendarId, HttpSession session) throws IOException, GeneralSecurityException {
        String userEmail = (String) session.getAttribute("userEmail");
        if (userEmail == null) throw new RuntimeException("Not authenticated");

        Calendar service = CalServiceBuilder.buildService(userEmail);
        Events response = service.events().list(calendarId)
                .setMaxResults(10)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();

        return response.getItems();
    }

    @Override
    public List<Event> displayCalendar(String calendarId, HttpSession session) throws IOException, GeneralSecurityException {
        return getEvents(calendarId, session);
    }

    @Override
    public Event addEvent(String calendarId, Map<String, Object> data, HttpSession session) throws IOException, GeneralSecurityException {
        String userEmail = (String) session.getAttribute("userEmail");
        if (userEmail == null) throw new RuntimeException("Not authenticated");

        Calendar service = CalServiceBuilder.buildService(userEmail);

        String summary     = (String) data.get("summary");
        String location    = (String) data.get("location");
        String description = (String) data.get("description");
        String startRaw    = ((Map<?, ?>) data.get("start")).get("dateTime").toString();
        String endRaw      = ((Map<?, ?>) data.get("end")).get("dateTime").toString();

        EventDateTime start = DateAdapter.parseEventDateTime(startRaw, ZoneId.of("America/New_York"));
        EventDateTime end   = DateAdapter.parseEventDateTime(endRaw,   ZoneId.of("America/New_York"));

        Event e = new Event()
                .setSummary(summary)
                .setLocation(location)
                .setDescription(description)
                .setStart(start)
                .setEnd(end);

        return service.events().insert(calendarId, e).execute();
    }

    @Override
    public boolean removeEvent(String calendarId, Event event, HttpSession session) throws IOException, GeneralSecurityException {
        String userEmail = (String) session.getAttribute("userEmail");
        if (userEmail == null) throw new RuntimeException("Not authenticated");

        Calendar service = CalServiceBuilder.buildService(userEmail);
        try {
            service.events().delete(calendarId, event.getId()).execute();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
