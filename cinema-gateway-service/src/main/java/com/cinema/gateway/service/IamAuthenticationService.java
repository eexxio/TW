package com.cinema.gateway.service;

import com.cinema.gateway.config.IamConfig;
import com.cinema.gateway.config.IamServiceAccountMapping;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class IamAuthenticationService {

    private static final Logger logger = LoggerFactory.getLogger(IamAuthenticationService.class);

    @Autowired
    private IamConfig iamConfig;

    @Autowired
    private IamServiceAccountMapping serviceAccountMapping;

    public ServiceAccountInfo validateAndExtractServiceAccount(String base64EncodedKey) {
        try {
            byte[] decodedKey = Base64.getDecoder().decode(base64EncodedKey);
            String jsonKey = new String(decodedKey, StandardCharsets.UTF_8);

            JsonObject keyObject = JsonParser.parseString(jsonKey).getAsJsonObject();

            if (!keyObject.has("client_email") || !keyObject.has("project_id") ||
                !keyObject.has("private_key") || !keyObject.has("type")) {
                logger.error("IAM Authentication failed: Invalid service account key format");
                return new ServiceAccountInfo(null, null, null, false);
            }

            String type = keyObject.get("type").getAsString();
            if (!"service_account".equals(type)) {
                logger.error("IAM Authentication failed: Not a service account key");
                return new ServiceAccountInfo(null, null, null, false);
            }

            String serviceAccountEmail = keyObject.get("client_email").getAsString();
            String projectId = keyObject.get("project_id").getAsString();

            ByteArrayInputStream credentialStream = new ByteArrayInputStream(decodedKey);
            GoogleCredentials credentials = GoogleCredentials.fromStream(credentialStream);

            String role = serviceAccountMapping.mapServiceAccountToRole(serviceAccountEmail);

            logger.info("IAM Authentication successful for service account: {} mapped to role: {}",
                       serviceAccountEmail, role);

            return new ServiceAccountInfo(serviceAccountEmail, role, projectId, true);

        } catch (Exception e) {
            logger.error("IAM Authentication failed: {}", e.getMessage());
            return new ServiceAccountInfo(null, null, null, false);
        }
    }

    public static class ServiceAccountInfo {
        private final String email;
        private final String role;
        private final String projectId;
        private final boolean valid;

        public ServiceAccountInfo(String email, String role, String projectId, boolean valid) {
            this.email = email;
            this.role = role;
            this.projectId = projectId;
            this.valid = valid;
        }

        public String getEmail() {
            return email;
        }

        public String getRole() {
            return role;
        }

        public String getProjectId() {
            return projectId;
        }

        public boolean isValid() {
            return valid;
        }
    }
}
