package org.tricol.supplierchain.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.tricol.supplierchain.dto.request.KeycloakLoginRequest;
import org.tricol.supplierchain.dto.request.KeycloakRegisterRequest;
import org.tricol.supplierchain.dto.response.KeycloakRegisterResponse;
import org.tricol.supplierchain.dto.response.KeycloakTokenResponse;
import org.tricol.supplierchain.service.inter.KeycloakService;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth/keycloak")
@RequiredArgsConstructor
public class KeycloakAuthController {

    private final KeycloakService keycloakService;

    @PostMapping("/register")
    public ResponseEntity<KeycloakRegisterResponse> register(@Valid @RequestBody KeycloakRegisterRequest request) {
        KeycloakRegisterResponse response = keycloakService.registerUser(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<KeycloakTokenResponse> login(@Valid @RequestBody KeycloakLoginRequest request) {
        KeycloakTokenResponse response = keycloakService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<KeycloakTokenResponse> refreshToken(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refresh_token");
        if (refreshToken == null || refreshToken.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        KeycloakTokenResponse response = keycloakService.refreshToken(refreshToken);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(@RequestBody Map<String, String> request) {
        String refreshToken = request.get("refresh_token");
        if (refreshToken == null || refreshToken.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        keycloakService.logout(refreshToken);
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }
}

