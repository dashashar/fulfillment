package ru.itis.fulfillment.impl.config;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.auth.http.HttpCredentialsAdapter;
import com.google.auth.oauth2.GoogleCredentials;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import ru.itis.fulfillment.impl.exception.GoogleSheetsInitializationException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

@Configuration
@Slf4j
public class AppConfig {

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
        return restTemplate;
    }

    @Bean
    public Sheets sheetsService(@Value("${google.spreadsheet.id}") String spreadsheetId,
                                @Value("${google.credentials}") String gcpJson) {
        if (spreadsheetId == null || spreadsheetId.isEmpty()) {
            log.error("Spreadsheet id cannot be null or empty");
            throw new GoogleSheetsInitializationException("Spreadsheet id cannot be null or empty");
        }
        if (gcpJson == null || gcpJson.isEmpty()) {
            log.error("Credentials json cannot be null or empty");
            throw new GoogleSheetsInitializationException("Credentials json cannot be null or empty");
        }
        try (InputStream credentialsStream = new ByteArrayInputStream(gcpJson.getBytes(StandardCharsets.UTF_8))) {
            GoogleCredentials credentials = GoogleCredentials.fromStream(credentialsStream)
                    .createScoped(Collections.singleton(SheetsScopes.SPREADSHEETS));
            return new Sheets.Builder(
                    GoogleNetHttpTransport.newTrustedTransport(),
                    GsonFactory.getDefaultInstance(),
                    new HttpCredentialsAdapter(credentials))
                    .setApplicationName("SpringFulfillmentApp")
                    .build();
        } catch (Exception e) {
            log.error("Failed to initialize Google Sheets client");
            throw new GoogleSheetsInitializationException("Failed to initialize Google Sheets client", e);
        }
    }

}