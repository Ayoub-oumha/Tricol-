package org.tricol.supplierchain.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestController {

    @GetMapping("/public")
    public ResponseEntity<Map<String, String>> publicEndpoint() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "This is a public endpoint - No authentication required");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/authenticated")
    public ResponseEntity<Map<String, Object>> authenticatedEndpoint(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "You are authenticated!");
        response.put("username", authentication.getName());
        response.put("authorities", authentication.getAuthorities());
        response.put("principal", authentication.getPrincipal().getClass().getName());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/fournisseur/read")
    @PreAuthorize("hasAuthority('FOURNISSEUR_READ')")
    public ResponseEntity<Map<String, Object>> readFournisseur(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "You have FOURNISSEUR_READ permission");
        response.put("user", authentication.getName());
        response.put("authorities", authentication);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/fournisseur/create")
    @PreAuthorize("hasAuthority('FOURNISSEUR_CREATE')")
    public ResponseEntity<Map<String, String>> createFournisseur(Authentication authentication) {
        Map<String, String> response = new HashMap<>();
        response.put("message", "You have FOURNISSEUR_CREATE permission");
        response.put("user", authentication.getName());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/produit/read")
    @PreAuthorize("hasAuthority('PRODUIT_READ')")
    public ResponseEntity<Map<String, String>> readProduit(Authentication authentication) {
        Map<String, String> response = new HashMap<>();
        response.put("message", "You have PRODUIT_READ permission");
        response.put("user", authentication.getName());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/produit/create")
    @PreAuthorize("hasAuthority('PRODUIT_CREATE')")
    public ResponseEntity<Map<String, String>> createProduit(Authentication authentication) {
        Map<String, String> response = new HashMap<>();
        response.put("message", "You have PRODUIT_CREATE permission");
        response.put("user", authentication.getName());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin/users")
    @PreAuthorize("hasAuthority('USER_MANAGE')")
    public ResponseEntity<Map<String, String>> manageUsers(Authentication authentication) {
        Map<String, String> response = new HashMap<>();
        response.put("message", "You have USER_MANAGE permission - Admin only");
        response.put("user", authentication.getName());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin/audit")
    @PreAuthorize("hasAuthority('AUDIT_READ')")
    public ResponseEntity<Map<String, Object>> readAudit(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "You have AUDIT_READ permission - Admin only");
        response.put("user", authentication.getName());
        response.put("authorities", authentication);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/bon-sortie/create")
    @PreAuthorize("hasAuthority('BON_SORTIE_CREATE')")
    public ResponseEntity<Map<String, Object>> createBonSortie(Authentication authentication) {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "You have BON_SORTIE_CREATE permission");
        response.put("user", authentication.getName());
        response.put("authorities", authentication);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/bon-sortie/validate")
    @PreAuthorize("hasAuthority('BON_SORTIE_VALIDATE')")
    public ResponseEntity<Map<String, String>> validateBonSortie(Authentication authentication) {
        Map<String, String> response = new HashMap<>();
        response.put("message", "You have BON_SORTIE_VALIDATE permission");
        response.put("user", authentication.getName());
        return ResponseEntity.ok(response);
    }
}
