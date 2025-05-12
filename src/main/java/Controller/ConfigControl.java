package Controller;

import ConfigAndUtil.Authorization;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.InputStream;

@RestController
public class ConfigControl {

    @GetMapping("/google-client-id")
    public String getGoogleClientId() {
        try {
            ClassPathResource resource = new ClassPathResource("credentials.json");
            InputStream inputStream = resource.getInputStream();

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(inputStream);

            String clientId = jsonNode.get("web").get("client_id").asText();
            return clientId;
        } catch (Exception e) {
            e.printStackTrace();
            return "Error loading client ID";
        }
    }


}
