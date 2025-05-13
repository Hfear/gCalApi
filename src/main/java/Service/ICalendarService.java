package Service;

import com.google.api.services.calendar.model.Event;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Map;

public interface ICalendarService {

    List<Event> getEvents(String calendarId, HttpSession session) throws IOException, GeneralSecurityException;
    List<Event> displayCalendar(String calendarId, HttpSession session) throws IOException, GeneralSecurityException;
    Event addEvent(String calendarId, Map<String, Object> data, HttpSession session) throws IOException, GeneralSecurityException;
    boolean removeEvent(String calendarId, Event event, HttpSession session) throws IOException, GeneralSecurityException;
}
