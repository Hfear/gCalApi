package ConfigAndUtil;

import ConfigAndUtil.Authorization;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.calendar.Calendar;
import com.google.api.client.auth.oauth2.Credential;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class CalServiceBuilder {

    private static final String APPLICATION_NAME = "Gcal API TESTING";

    public static Calendar buildService(String userId) throws GeneralSecurityException, IOException {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        Credential credentials = Authorization.getUserCredentials(userId);

        return new Calendar.Builder(HTTP_TRANSPORT, GsonFactory.getDefaultInstance(), credentials)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }


}
