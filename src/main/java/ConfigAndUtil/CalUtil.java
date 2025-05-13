package ConfigAndUtil;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;
import java.util.List;
import ConfigAndUtil.CalServiceBuilder;

public class CalUtil {

    public static String getUpcomingEvents(String userId) {
        try {
            Calendar service = CalServiceBuilder.buildService(userId);
            DateTime now = new DateTime(System.currentTimeMillis());

            Events events = service.events().list("primary")
                    .setMaxResults(10)
                    .setTimeMin(now)
                    .setOrderBy("startTime")
                    .setSingleEvents(true)
                    .execute();

            List<Event> items = events.getItems();
            if (items.isEmpty()) return "No upcoming events found.";

            StringBuilder eventDetails = new StringBuilder("Upcoming events:\n");
            for (Event event : items) {
                DateTime start = event.getStart().getDateTime();
                if (start == null) start = event.getStart().getDate();
                eventDetails.append(String.format("%s (%s)\n", event.getSummary(), start));
            }
            return eventDetails.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error retrieving events";
        }
    }

}
