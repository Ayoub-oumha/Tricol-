package org.tricol.supplierchain.security;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.tricol.supplierchain.entity.UserApp;
import org.tricol.supplierchain.repository.UserRepository;
import org.tricol.supplierchain.service.CurrentUserService;
import org.tricol.supplierchain.service.inter.AuditService;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class AdminAuditAspect {

    private final AuditService auditService;
    private final CurrentUserService currentUserService;

    @AfterReturning(
            pointcut = "execution(* org.tricol.supplierchain.service.UserAdminServiceImpl.assignRole(..))",
            returning = "result"
    )
    public void auditRoleAssignment(JoinPoint joinPoint, UserApp result) {
        try {
            UserApp admin = currentUserService.getCurrentUser();
            String ipAddress = getClientIpAddress();

            Object[] args = joinPoint.getArgs();
            Long targetUserId = (Long) args[0];

            auditService.logAudit(
                    admin != null ? admin.getId() : null,
                    admin != null ? admin.getUsername() : "unknown",
                    "ASSIGN_ROLE",
                    "USER_MANAGEMENT",
                    String.format("Assigned role '%s' to user ID: %d", result.getRole(), targetUserId),
                    ipAddress
            );
        } catch (Exception e) {
            log.error("Failed to audit role assignment: {}", e.getMessage());
        }
    }

    @AfterReturning(
            pointcut = "execution(* org.tricol.supplierchain.service.UserAdminServiceImpl.modifyPermission(..))"
    )
    public void auditPermissionModification(JoinPoint joinPoint) {
        try {
            UserApp admin = currentUserService.getCurrentUser();
            String ipAddress = getClientIpAddress();

            Object[] args = joinPoint.getArgs();
            Long targetUserId = (Long) args[0];

            auditService.logAudit(
                    admin != null ? admin.getId() : null,
                    admin != null ? admin.getUsername() : "unknown",
                    "MODIFY_PERMISSION",
                    "USER_MANAGEMENT",
                    String.format("Modified permission for user ID: %d", targetUserId),
                    ipAddress
            );
        } catch (Exception e) {
            log.error("Failed to audit permission modification: {}", e.getMessage());
        }
    }

    private String getClientIpAddress() {
        try {
            ServletRequestAttributes attributes = 
                    (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
            HttpServletRequest request = attributes.getRequest();

            String xForwardedFor = request.getHeader("X-Forwarded-For");
            if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
                return xForwardedFor.split(",")[0].trim();
            }

            String xRealIp = request.getHeader("X-Real-IP");
            if (xRealIp != null && !xRealIp.isEmpty()) {
                return xRealIp;
            }

            return request.getRemoteAddr();
        } catch (Exception e) {
            return "unknown";
        }
    }
}
