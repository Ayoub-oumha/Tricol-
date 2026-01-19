package org.tricol.supplierchain.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.tricol.supplierchain.dto.request.KeycloakLoginRequest;
import org.tricol.supplierchain.dto.request.KeycloakRegisterRequest;
import org.tricol.supplierchain.dto.response.KeycloakRegisterResponse;
import org.tricol.supplierchain.dto.response.KeycloakTokenResponse;
import org.tricol.supplierchain.exception.BusinessException;
import org.tricol.supplierchain.service.inter.KeycloakService;

import java.util.*;

@Slf4j
@Service
public class KeycloakServiceImpl implements KeycloakService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    @Value("${keycloak.auth-server-url:http://localhost:8080}")
    private String keycloakServerUrl;

    @Value("${keycloak.realm:Tricol}")
    private String realm;

    @Value("${keycloak.client-id:supplier-chain-api}")
    private String clientId;

    @Value("${keycloak.client-secret:}")
    private String clientSecret;

    @Value("${keycloak.admin.username:admin}")
    private String adminUsername;

    @Value("${keycloak.admin.password:admin}")
    private String adminPassword;

    public KeycloakServiceImpl(ObjectMapper objectMapper) {
        this.restTemplate = new RestTemplate();
        this.objectMapper = objectMapper;
    }

    @Override
    public KeycloakRegisterResponse registerUser(KeycloakRegisterRequest request) {
        try {
            String adminToken = getAdminAccessToken();

            String usersUrl = keycloakServerUrl + "/admin/realms/" + realm + "/users";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(adminToken);

            Map<String, Object> userRepresentation = new HashMap<>();
            userRepresentation.put("username", request.getUsername());
            userRepresentation.put("email", request.getEmail());
            userRepresentation.put("firstName", request.getFirstName());
            userRepresentation.put("lastName", request.getLastName());
            userRepresentation.put("enabled", true);
            userRepresentation.put("emailVerified", true);

            // Set password credentials
            Map<String, Object> credentials = new HashMap<>();
            credentials.put("type", "password");
            credentials.put("value", request.getPassword());
            credentials.put("temporary", false);
            userRepresentation.put("credentials", Collections.singletonList(credentials));

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(userRepresentation, headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    usersUrl,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            if (response.getStatusCode() == HttpStatus.CREATED) {
                // Extract user ID from Location header
                String locationHeader = response.getHeaders().getFirst("Location");
                String userId = locationHeader != null ? locationHeader.substring(locationHeader.lastIndexOf("/") + 1) : null;

                log.info("User registered successfully in Keycloak: {}", request.getUsername());

                return KeycloakRegisterResponse.builder()
                        .status("SUCCESS")
                        .message("User registered successfully in Keycloak")
                        .userId(userId)
                        .username(request.getUsername())
                        .email(request.getEmail())
                        .build();
            }

            throw new BusinessException("Failed to register user in Keycloak");

        } catch (HttpClientErrorException e) {
            log.error("Keycloak registration error: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            if (e.getStatusCode() == HttpStatus.CONFLICT) {
                throw new BusinessException("User already exists in Keycloak with username: " + request.getUsername());
            }
            throw new BusinessException("Failed to register user in Keycloak: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error during Keycloak registration", e);
            throw new BusinessException("Failed to register user in Keycloak: " + e.getMessage());
        }
    }

    @Override
    public KeycloakTokenResponse login(KeycloakLoginRequest request) {
        try {
            String tokenUrl = keycloakServerUrl + "/realms/" + realm + "/protocol/openid-connect/token";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("grant_type", "password");
            body.add("client_id", clientId);
            body.add("username", request.getUsername());
            body.add("password", request.getPassword());

            if (clientSecret != null && !clientSecret.isEmpty()) {
                body.add("client_secret", clientSecret);
            }

            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);

            ResponseEntity<KeycloakTokenResponse> response = restTemplate.exchange(
                    tokenUrl,
                    HttpMethod.POST,
                    entity,
                    KeycloakTokenResponse.class
            );

            log.info("User logged in successfully via Keycloak: {}", request.getUsername());
            return response.getBody();

        } catch (HttpClientErrorException e) {
            log.error("Keycloak login error: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                throw new BusinessException("Invalid username or password");
            }
            throw new BusinessException("Failed to authenticate with Keycloak: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error during Keycloak login", e);
            throw new BusinessException("Failed to authenticate with Keycloak: " + e.getMessage());
        }
    }

    @Override
    public KeycloakTokenResponse refreshToken(String refreshToken) {
        try {
            String tokenUrl = keycloakServerUrl + "/realms/" + realm + "/protocol/openid-connect/token";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("grant_type", "refresh_token");
            body.add("client_id", clientId);
            body.add("refresh_token", refreshToken);

            if (clientSecret != null && !clientSecret.isEmpty()) {
                body.add("client_secret", clientSecret);
            }

            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);

            ResponseEntity<KeycloakTokenResponse> response = restTemplate.exchange(
                    tokenUrl,
                    HttpMethod.POST,
                    entity,
                    KeycloakTokenResponse.class
            );

            log.info("Token refreshed successfully via Keycloak");
            return response.getBody();

        } catch (HttpClientErrorException e) {
            log.error("Keycloak token refresh error: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new BusinessException("Failed to refresh token: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error during Keycloak token refresh", e);
            throw new BusinessException("Failed to refresh token: " + e.getMessage());
        }
    }

    @Override
    public void logout(String refreshToken) {
        try {
            String logoutUrl = keycloakServerUrl + "/realms/" + realm + "/protocol/openid-connect/logout";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("client_id", clientId);
            body.add("refresh_token", refreshToken);

            if (clientSecret != null && !clientSecret.isEmpty()) {
                body.add("client_secret", clientSecret);
            }

            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);

            restTemplate.exchange(
                    logoutUrl,
                    HttpMethod.POST,
                    entity,
                    Void.class
            );

            log.info("User logged out successfully via Keycloak");

        } catch (Exception e) {
            log.error("Error during Keycloak logout", e);
            throw new BusinessException("Failed to logout from Keycloak: " + e.getMessage());
        }
    }

    private String getAdminAccessToken() {
        try {
            String tokenUrl = keycloakServerUrl + "/realms/master/protocol/openid-connect/token";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("grant_type", "password");
            body.add("client_id", "admin-cli");
            body.add("username", adminUsername);
            body.add("password", adminPassword);

            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);

            ResponseEntity<JsonNode> response = restTemplate.exchange(
                    tokenUrl,
                    HttpMethod.POST,
                    entity,
                    JsonNode.class
            );

            if (response.getBody() != null) {
                return response.getBody().get("access_token").asText();
            }

            throw new BusinessException("Failed to get admin access token from Keycloak");

        } catch (HttpClientErrorException e) {
            log.error("Failed to get admin token: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new BusinessException("Failed to authenticate admin with Keycloak: " + e.getMessage());
        }
    }
}

