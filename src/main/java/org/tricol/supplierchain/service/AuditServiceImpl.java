package org.tricol.supplierchain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.scheduling.annotation.Async;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tricol.supplierchain.entity.AuditLog;
import org.tricol.supplierchain.entity.UserApp;
import org.tricol.supplierchain.repository.AuditLogRepository;
import org.tricol.supplierchain.repository.UserRepository;
import org.tricol.supplierchain.security.CustomUserDetails;
import org.tricol.supplierchain.service.inter.AuditService;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuditServiceImpl implements AuditService {

    private final AuditLogRepository auditLogRepository;
    private final UserRepository userRepository;

    @Async
    @Transactional
    @Override
    public void log(String action, String resource, String resourceId, String details) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            Long userId = null;
            String username = "anonymous";

            if (authentication != null && authentication.isAuthenticated()
                && !(authentication.getPrincipal() instanceof String)) {

                if (authentication.getPrincipal() instanceof CustomUserDetails userDetails) {
                    UserApp user = userDetails.getUser();
                    userId = user.getId();
                    username = user.getUsername();
                }
            }

            logWithUser(userId, username, action, resource, resourceId, details);
        } catch (Exception e) {
            log.error("Error logging audit: {}", e.getMessage());
        }
    }

    @Async
    @Transactional
    @Override
    public void logWithUser(Long userId, String username, String action, String resource, String resourceId, String details) {
        try {
            AuditLog auditLog = AuditLog.builder()
                    .userId(userId)
                    .username(username)
                    .action(action)
                    .resource(resource)
                    .resourceId(resourceId)
                    .details(details)
                    .createdAt(LocalDateTime.now())
                    .build();

            auditLogRepository.save(auditLog);
            log.info("Audit log created: {} - {} - {}", username, action, resource);
        } catch (Exception e) {
            log.error("Error saving audit log: {}", e.getMessage());
        }
    }

    @Async
    @Transactional
    @Override
    public void logAuthentication(String username, String action, boolean success) {
        try {
            String details = success ? "Authentication successful" : "Authentication failed";

            Long userId = null;
            if (success) {
                userId = userRepository.findByUsername(username)
                        .map(UserApp::getId)
                        .orElse(null);
            }

            AuditLog auditLog = AuditLog.builder()
                    .userId(userId)
                    .username(username)
                    .action(action)
                    .resource("AUTHENTICATION")
                    .resourceId(null)
                    .details(details)
                    .createdAt(LocalDateTime.now())
                    .build();

            auditLogRepository.save(auditLog);
            log.info("Authentication audit logged: {} - {}", username, action);
        } catch (Exception e) {
            log.error("Error logging authentication: {}", e.getMessage());
        }
    }

    @Async
    @Transactional
    @Override
    public void logPermissionChange(Long userId, String username, String permissionName, boolean granted, Long grantedBy) {
        try {
            String action = granted ? "PERMISSION_GRANTED" : "PERMISSION_REVOKED";
            String details = String.format("Permission '%s' %s by user ID: %d",
                    permissionName,
                    granted ? "granted" : "revoked",
                    grantedBy);

            AuditLog auditLog = AuditLog.builder()
                    .userId(userId)
                    .username(username)
                    .action(action)
                    .resource("USER_PERMISSION")
                    .resourceId(userId.toString())
                    .details(details)
                    .createdAt(LocalDateTime.now())
                    .build();

            auditLogRepository.save(auditLog);
            log.info("Permission change logged: {} - {} - {}", username, action, permissionName);
        } catch (Exception e) {
            log.error("Error logging permission change: {}", e.getMessage());
        }
    }
}
