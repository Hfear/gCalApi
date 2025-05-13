package ConfigAndUtil;

import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.EventDateTime;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.TimeZone;

public class DateAdapter {

    private static final DateTimeFormatter[] FORMATTERS = new DateTimeFormatter[]{
            DateTimeFormatter.ISO_LOCAL_DATE_TIME,
            DateTimeFormatter.ofPattern("M/d/yyyy h:mm a"),
            DateTimeFormatter.ofPattern("M/d/yyyy H:mm"),
            DateTimeFormatter.ofPattern("yyyy-MM-dd H:mm")
    };

    public static EventDateTime parseEventDateTime(String input) {
        return parseEventDateTime(input, ZoneId.systemDefault());
    }

    public static EventDateTime parseEventDateTime(String input, ZoneId zone) {
        LocalDateTime ldt = null;
        for (DateTimeFormatter fmt : FORMATTERS) {
            try {
                ldt = LocalDateTime.parse(input, fmt);
                break;
            } catch (DateTimeParseException ignored) {}
        }

        DateTime dt;
        if (ldt == null) {
            // fallback to full ISO instant
            Instant inst = Instant.parse(input);
            Date utilDate = Date.from(inst);
            TimeZone tz = TimeZone.getTimeZone(zone);
            dt = new DateTime(utilDate, tz);
        } else {
            ZonedDateTime zdt = ldt.atZone(zone);
            Date utilDate = Date.from(zdt.toInstant());
            TimeZone tz = TimeZone.getTimeZone(zone);
            dt = new DateTime(utilDate, tz);
        }

        return new EventDateTime()
                .setDateTime(dt)
                .setTimeZone(zone.toString());
    }
}
