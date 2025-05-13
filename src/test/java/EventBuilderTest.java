import Service.EventBuilder;

import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventReminder;
import com.google.api.services.calendar.model.Event.Reminders;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EventBuilderTest {

    @Test
    void testBuildSimpleEvent() {
        EventBuilder builder = new EventBuilder();
        Event event = builder
                .setSummary("Meeting")
                .setLocation("Room 101")
                .setDescription("Quarterly planning")
                .setStart("2025-05-08T10:00:00-04:00", "America/New_York")
                .setEnd("2025-05-08T11:00:00-04:00", "America/New_York")
                .build();

        assertEquals("Meeting", event.getSummary());
        assertEquals("Room 101", event.getLocation());
        assertEquals("Quarterly planning", event.getDescription());
        assertNotNull(event.getStart());
        assertNotNull(event.getEnd());
    }

    @Test
    void testSetAttendeesAndReminders() {
        List<String> attendees = List.of("alice@example.com", "bob@example.com");
        List<EventReminder> reminders = List.of(
                new EventReminder().setMethod("email").setMinutes(30),
                new EventReminder().setMethod("popup").setMinutes(10)
        );

        Event event = new EventBuilder()
                .setSummary("Test")
                .setAttendees(attendees)
                .setReminders(reminders)
                .build();

        List<EventAttendee> resultAttendees = event.getAttendees();
        assertEquals(2, resultAttendees.size());
        assertEquals("alice@example.com", resultAttendees.get(0).getEmail());

        Reminders resultReminders = event.getReminders();
        assertNotNull(resultReminders);
        assertFalse(resultReminders.getUseDefault());
        assertEquals(2, resultReminders.getOverrides().size());
    }
}
