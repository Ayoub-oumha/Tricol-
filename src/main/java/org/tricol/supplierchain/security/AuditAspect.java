package org.tricol.supplierchain.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.tricol.supplierchain.entity.UserApp;
import org.tricol.supplierchain.repository.UserRepository;
import org.tricol.supplierchain.service.CurrentUserService;
import org.tricol.supplierchain.service.inter.AuditService;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class AuditAspect {

    private final AuditService auditService;
    private final CurrentUserService currentUserService;
    private final ObjectMapper objectMapper;

    @Around("@annotation(org.tricol.supplierchain.security.RequirePermission)")
    public Object auditPermissionAccess(ProceedingJoinPoint joinPoint) throws Throwable {

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RequirePermission requirePermission = method.getAnnotation(RequirePermission.class);


        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication != null ? authentication.getName() : "anonymous";
        Long userId = null;

        if (authentication != null && authentication.isAuthenticated()) {
            try {
                UserApp user = currentUserService.getCurrentUser();
                userId = user != null ? user.getId() : null;
                username = user != null ? user.getUsername() : username;
            } catch (Exception e) {
                log.warn("Failed to retrieve user ID for audit: {}", e.getMessage());
            }
        }


        String ipAddress = getClientIpAddress();


        String resource = getResourceFromMethod(method);
        String action = method.getName();
        String details = buildAuditDetails(joinPoint, requirePermission.value());

        Object result;
        try {
            result = joinPoint.proceed();

            auditService.logAudit(
                    userId,
                    username,
                    action,
                    resource,
                    details + " - Status: SUCCESS",
                     ipAddress);

            return result;
        } catch (Exception e) {

            auditService.logAudit(userId, username, action, resource, 
                    details + " - Status: FAILED - Error: " + e.getMessage(), ipAddress);
            throw e;
        }
    }

    private String getResourceFromMethod(Method method) {
        String className = method.getDeclaringClass().getSimpleName();
        return className.replace("Controller", "").toUpperCase();
    }

    private String buildAuditDetails(ProceedingJoinPoint joinPoint, String permission) {
        try {
            Map<String, Object> details = new HashMap<>();
            details.put("permission", permission);
            details.put("method", joinPoint.getSignature().getName());
            details.put("class", joinPoint.getSignature().getDeclaringTypeName());


            Object[] args = joinPoint.getArgs();
            if (args != null && args.length > 0) {
                for (int i = 0; i < args.length; i++) {
                    Object arg = args[i];
                    if (arg != null && !isSensitiveType(arg)) {
                        details.put("arg" + i, arg.toString());
                    }
                }
            }

            return objectMapper.writeValueAsString(details);
        } catch (Exception e) {
            log.error("Failed to build audit details: {}", e.getMessage());
            return "Permission: " + permission;
        }
    }

    private boolean isSensitiveType(Object arg) {
        return arg instanceof HttpServletRequest || 
               arg.getClass().getName().contains("Response") ||
               arg.getClass().getName().contains("Password");
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
            log.warn("Failed to get client IP address: {}", e.getMessage());
            return "unknown";
        }
    }
}
