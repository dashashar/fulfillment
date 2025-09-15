package ru.itis.fulfillment.impl.security.validator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.itis.fulfillment.impl.exception.AuthenticationServiceException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.stream.Collectors;

import java.util.Arrays;
import java.util.Map;

import static javax.xml.crypto.dsig.SignatureMethod.HMAC_SHA256;

@Component
@Slf4j
public class TelegramAuthValidator {

    private static final long MAX_DATA_AGE_SECONDS = 86400;
    private static final String HMAC_ALGORITHM = "HmacSHA256";

    private final String botToken;
    private final ObjectMapper objectMapper;

    public TelegramAuthValidator(@Value("${telegram.bot.token}") String botToken,
                                 ObjectMapper objectMapper) {
        this.botToken = botToken;
        this.objectMapper = objectMapper;
    }

    public long validate(String initData) {
        if (initData == null || initData.isBlank()) {
            log.warn("Telegram init data is missing");
            throw new AuthenticationServiceException("Telegram init data is missing");
        }
        Map<String, String> parsedData = parseInitData(initData);
        if (parsedData == null || !parsedData.containsKey("hash") ||
                !parsedData.containsKey("user") || !parsedData.containsKey("auth_date")) {
            log.warn("Invalid telegram init data, missing required fields: {}", initData);
            throw new AuthenticationServiceException("Missing required fields in init data: hash, user or auth_date");
        }
        checkAuthDate(parsedData.get("auth_date"), parsedData.get("user"));

        String receivedHash = parsedData.get("hash");
        String dataCheckString = buildDataCheckString(parsedData);
        byte[] secretKey = getSecretKey();
        if (!isHashValid(dataCheckString, receivedHash, secretKey)) {
            log.error("Invalid telegram hash for data: {}", parsedData);
            throw new AuthenticationServiceException("Invalid telegram hash");
        }
        return parseUser(parsedData.get("user"));
    }

    private Map<String, String> parseInitData(String initData) {
        try {
            return Arrays.stream(initData.split("&"))
                    .map(pair -> pair.split("="))
                    .collect(Collectors.toMap(
                            arr -> URLDecoder.decode(arr[0], StandardCharsets.UTF_8),
                            arr -> arr.length > 1 ? URLDecoder.decode(arr[1], StandardCharsets.UTF_8) : ""
                    ));
        } catch (Exception e) {
            log.warn("Failed to parse initData: {} exception message: {}", initData, e.getMessage());
            throw new AuthenticationServiceException("Failed to parse initData", e);
        }
    }

    private void checkAuthDate(String authDateStr, String userData) {
        try {
            long authDate = Long.parseLong(authDateStr);
            long currentTime = Instant.now().getEpochSecond();

            if (authDate < currentTime - MAX_DATA_AGE_SECONDS) {
                log.warn("Init data expired for user: {}", userData);
                throw new AuthenticationServiceException("Init data expired. Log in again via Telegram");
            }
        } catch (NumberFormatException e) {
            log.warn("Invalid auth date format: {} for user: {}", authDateStr, userData);
            throw new AuthenticationServiceException("Invalid auth date format", e);
        }
    }

    private String buildDataCheckString(Map<String, String> params) {
        return params.entrySet().stream()
                .filter(entry -> !entry.getKey().equals("hash"))
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> String.format("%s=%s", entry.getKey(), entry.getValue()))
                .collect(Collectors.joining("\n"));
    }

    private byte[] getSecretKey() {
        try {
            Mac sha256 = Mac.getInstance(HMAC_ALGORITHM);
            SecretKeySpec secretKeySpec = new SecretKeySpec("WebAppData".getBytes(StandardCharsets.UTF_8), HMAC_SHA256);
            sha256.init(secretKeySpec);
            return sha256.doFinal(botToken.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            log.error("Failed to generate secret key: {}", e.getMessage());
            throw new AuthenticationServiceException("Failed to validate hash", e);
        }
    }

    private boolean isHashValid(String dataCheckString, String receivedHash, byte[] secretKey) {
        try {
            Mac hmacSha256 = Mac.getInstance(HMAC_ALGORITHM);
            hmacSha256.init(new SecretKeySpec(secretKey, HMAC_ALGORITHM));
            byte[] computedHash = hmacSha256.doFinal(dataCheckString.getBytes(StandardCharsets.UTF_8));
            String computedHashHex = bytesToHex(computedHash);
            return receivedHash.equals(computedHashHex);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            log.error("Failed to validate hash: {}", e.getMessage());
            throw new AuthenticationServiceException("Failed to validate hash", e);
        }
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }

    private long parseUser(String userJson) {
        try {
            JsonNode idNode = objectMapper.readTree(userJson).get("id");
            if (idNode == null) {
                log.warn("Required id field is missing from init data: {}", userJson);
                throw new AuthenticationServiceException("Required id field is missing from init data");
            }
            if (!idNode.canConvertToLong()) {
                log.warn("Invalid account id in init data: {}", idNode);
                throw new AuthenticationServiceException("Invalid account id in init data");
            }
            return idNode.asLong();
        } catch (JsonProcessingException e) {
            log.warn("Failed to parse telegram account data: {} {}", userJson, e.getMessage());
            throw new AuthenticationServiceException("Failed to parse telegram account data", e);
        }
    }
}
