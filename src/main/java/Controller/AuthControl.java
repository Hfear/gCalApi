package Controller;

import Model.User;
import Database.UserDatabase;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.auth.oauth2.Credential;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Value;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Arrays;




import ConfigAndUtil.Authorization;

@RestController
public class AuthControl {

    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final List<String> SCOPES = Arrays.asList(
            "openid",
            "email",
            "profile",
            "https://www.googleapis.com/auth/calendar"
    );

    @Value("${frontend.redirect-uri}")
    private String frontredirectUri;

    @GetMapping("/auth/url")
    public String getAuthUrl() {
        try {
            return Authorization.getOAuthUrl();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error generating URL";
        }
    }

    @GetMapping("/oauth/callback")
    public void oauthCallback(@RequestParam("code") String code, HttpServletResponse response, HttpSession session) {
        System.out.println("    ****called oauthCallback***");
        try {
            InputStream in = Authorization.class.getResourceAsStream("/credentials.json");
            GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(
                    GsonFactory.getDefaultInstance(), new InputStreamReader(in)
            );

            String clientId = clientSecrets.getDetails().getClientId();
            String clientSecret = clientSecrets.getDetails().getClientSecret();
            String redirectUri = Authorization.REDIRECT_URI;

            GoogleTokenResponse tokenResponse = new GoogleAuthorizationCodeTokenRequest(
                    new NetHttpTransport(),
                    GsonFactory.getDefaultInstance(),
                    "https://oauth2.googleapis.com/token",
                    clientId,
                    clientSecret,
                    code,
                    redirectUri
            ).execute();

            // Getting user info from token returned from google
            String idTokenString = tokenResponse.getIdToken();
            if (idTokenString == null) {
                throw new RuntimeException("ID token is null. Cannot identify user.");
            }

            GoogleIdToken idToken = GoogleIdToken.parse(JSON_FACTORY, idTokenString);

            //storing user in our temp Database file
            String userEmail = idToken.getPayload().getEmail();
            String userName = idToken.getPayload().get("name").toString();
            User newUser = new User(0, userName, userEmail);
            if (UserDatabase.getUser(userEmail) == null) {
                UserDatabase.postUser(newUser);
                UserDatabase.persistUser(newUser);
                System.out.println("\n Saved Authenticated user: " + newUser.getName() + ", ID+Email: " + newUser.getUserId() + userEmail + "\n");
            }
            else {
                System.out.println("Returning user: " + userEmail);
            }


            //storing user email in tokens for call id in frontend
            session.setAttribute("userEmail", userEmail);
            System.out.println("Session ID in oauthCallback: " + session.getId());

            // storing in googles flow
            Authorization.getFlow().createAndStoreCredential(tokenResponse, userEmail);

            //send back to homepage
            response.sendRedirect(frontredirectUri);

        } catch (Exception e) {
            e.printStackTrace();
            try {
                response.sendRedirect("http://localhost:5174/error");
            } catch (Exception ignored) {}
        }
    }

    //checking if current user is authenticated from session
    @GetMapping("/oauth/isAuth")
    public boolean isAuth(HttpSession session) {
        String userId = (String) session.getAttribute("userEmail");
        try {
            Credential credential = Authorization.getUserCredentials(userId);
            return credential != null && credential.getAccessToken() != null;
        } catch (Exception e) {
            return false;
        }
    }

    //gets current user id from session
    @GetMapping("/currentUser")
    public int getCurrentUserId(HttpSession session) {
        String email = (String) session.getAttribute("userEmail");
        if (email == null) throw new RuntimeException("Not authenticated.");
        return UserDatabase.getUser(email).getUserId();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.ok().build();
    }

}
