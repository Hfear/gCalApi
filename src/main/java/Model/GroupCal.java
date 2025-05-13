package Model;

import com.google.api.services.calendar.model.Event;
import java.util.ArrayList;
import java.util.List;

public class GroupCal {
    private Long id;
    private String groupCode; // Code of the group this calendar belongs to
    private String calendarId; // Google Calendar ID
    private String ownerEmail; // owner "owns" group cal

    public GroupCal(Long id, String groupCode, String calendarId, String ownerEmail) {
        this.id = id;
        this.groupCode = groupCode;
        this.calendarId = calendarId;
        this.ownerEmail = ownerEmail;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getGroupCode() { return groupCode; }
    public void setGroupCode(String groupCode) { this.groupCode = groupCode; }

    public String getCalendarId() { return calendarId; }
    public void setCalendarId(String calendarId) { this.calendarId = calendarId; }

    public String getOwnerEmail() { return ownerEmail; }
    public void setOwnerEmail(String ownerEmail) { this.ownerEmail = ownerEmail;}

}

