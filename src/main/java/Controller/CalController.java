package Controller;

import ConfigAndUtil.Authorization;
import Service.EventBuilder;
import ConfigAndUtil.CalServiceBuilder;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.Events;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;


//***************************DELETE CLASS WHEN PHASED OUT ************************************************************
@RestController
public class CalController {

    private static final String APPLICATION_NAME = "Gcal API TESTING";
    private static Calendar service;
    //service = CalServiceBuilder.buildService();

//    static {
//        try {
//            // Initialize the service with OAuth credentials
//            final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
//            Credential credentials = Authorization.getCredentials(HTTP_TRANSPORT);
//            service = new Calendar.Builder(HTTP_TRANSPORT, GsonFactory.getDefaultInstance(), credentials)
//                    .setApplicationName(APPLICATION_NAME)
//                    .build();
//        } catch (GeneralSecurityException | IOException e) {
//            e.printStackTrace();
//        }
//    }

    // Fetch upcoming events from the user's calendar
    @CrossOrigin(origins = "http://localhost:5174", allowCredentials = "true")
    @GetMapping("/events")
    public String getUpcomingEvents(HttpSession session) throws IOException {
        System.out.println(" getUpcomingEvents called");
        String userId = (String) session.getAttribute("userEmail");
        System.out.println("Session ID in /events: " + session.getId());
        System.out.println(" userId = (String) session.getAttribute('userEmail') = " + userId);
        if (userId == null) return "User not authenticated";

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

    // Create a new event in the user's calendar
    @PostMapping("/createEvent")
    public String createEvent(HttpSession session) throws IOException {

        String userId = (String) session.getAttribute("userEmail");
        if (userId == null) return "User not authenticated";

        // Build an event using Cal service builder now
        try {
            Calendar service = CalServiceBuilder.buildService(userId);
            Event event = new EventBuilder()
                    .setSummary("Google I/O 2025")
                    .setLocation("800 Howard St., San Francisco, CA 94103")
                    .setDescription("A chance to hear more about Google's developer products.")
                    .setStart("2025-05-28T09:00:00-07:00", "America/Los_Angeles")
                    .setEnd("2025-05-28T17:00:00-07:00", "America/Los_Angeles")
                    .build();

            String calendarId = "primary";
            Event createdEvent = service.events().insert(calendarId, event).execute();

            return "Event created: " + createdEvent.getHtmlLink();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error creating event";
        }
    }
}