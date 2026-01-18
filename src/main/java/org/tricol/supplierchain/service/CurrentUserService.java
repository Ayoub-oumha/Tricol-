package org.tricol.supplierchain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tricol.supplierchain.entity.Role;
import org.tricol.supplierchain.entity.UserApp;
import org.tricol.supplierchain.repository.RoleRepository;
import org.tricol.supplierchain.repository.UserRepository;

import java.util.Collection;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CurrentUserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Transactional
    public UserApp getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }


        if (authentication.getPrincipal() instanceof Jwt jwt) {
            String keycloakUserId = jwt.getSubject();
            
            return userRepository.findByKeycloakUserId(keycloakUserId)
                    .orElseGet(() -> createKeycloakUser(jwt));
        }
        

        String username = authentication.getName();
        return userRepository.findByUsername(username).orElse(null);
    }

    private UserApp createKeycloakUser(Jwt jwt) {
        String keycloakUserId = jwt.getSubject();
        String username = jwt.getClaimAsString("preferred_username");
        String email = jwt.getClaimAsString("email");
        
        Role role = extractRoleFromJwt(jwt);

        UserApp user = UserApp.builder()
                .keycloakUserId(keycloakUserId)
                .username(username != null ? username : keycloakUserId)
                .email(email != null ? email : keycloakUserId + "@keycloak.local")
                .password(null)
                .enabled(true)
                .role(role)
                .build();

        UserApp savedUser = userRepository.save(user);
        log.info("Auto-created Keycloak user: {} (ID: {}) with role: {}", 
                username, keycloakUserId, role != null ? role.getName() : "none");
        
        return savedUser;
    }

    @SuppressWarnings("unchecked")
    private Role extractRoleFromJwt(Jwt jwt) {
        try {
            Map<String, Object> realmAccess = jwt.getClaim("realm_access");
            if (realmAccess != null) {
                Collection<String> roles = (Collection<String>) realmAccess.get("roles");
                if (roles != null) {
                    return roles.stream()
                            .filter(role -> role.startsWith("ROLE_"))
                            .findFirst()
                            .flatMap(roleRepository::findByName)
                            .orElse(null);
                }
            }
        } catch (Exception e) {
            log.error("Failed to extract role from JWT: {}", e.getMessage(), e);
        }
        return null;
    }
}
