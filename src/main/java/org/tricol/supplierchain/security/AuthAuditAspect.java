package org.tricol.supplierchain.security;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.tricol.supplierchain.dto.request.LoginRequest;
import org.tricol.supplierchain.dto.request.RegisterRequest;
import org.tricol.supplierchain.dto.response.LoginResponse;
import org.tricol.supplierchain.dto.response.RegisterResponse;
import org.tricol.supplierchain.entity.UserApp;
import org.tricol.supplierchain.repository.UserRepository;
import org.tricol.supplierchain.service.inter.AuditService;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class AuthAuditAspect {

    private final AuditService auditService;
    private final UserRepository userRepository;

    @AfterReturning(
            pointcut = "execution(* org.tricol.supplierchain.service.AuthServiceImpl.login(..))"
    )
    public void auditSuccessfulLogin(JoinPoint joinPoint) {
        try {
            Object[] args = joinPoint.getArgs();
            if (args.length > 0 && args[0] instanceof LoginRequest) {
                LoginRequest loginRequest = (LoginRequest) args[0];
                String ipAddress = getClientIpAddress();

                UserApp user = userRepository.findByUsername(loginRequest.getUsername()).orElse(null);

                auditService.logAudit(
                        user != null ? user.getId() : null,
                        loginRequest.getUsername(),
                        "LOGIN",
                        "AUTH",
                        "User logged in successfully",
                        ipAddress
                );
            }
        } catch (Exception e) {
            log.error("Failed to audit successful login: {}", e.getMessage());
        }
    }

    @AfterThrowing(
            pointcut = "execution(* org.tricol.supplierchain.service.AuthServiceImpl.login(..))",
            throwing = "error"
    )
    public void auditFailedLogin(JoinPoint joinPoint, Throwable error) {
        try {
            Object[] args = joinPoint.getArgs();
            if (args.length > 0 && args[0] instanceof LoginRequest) {
                LoginRequest loginRequest = (LoginRequest) args[0];
                String ipAddress = getClientIpAddress();

                auditService.logAudit(
                        null,
                        loginRequest.getUsername(),
                        "LOGIN_FAILED",
                        "AUTH",
                        "Failed login attempt: " + error.getMessage(),
                        ipAddress
                );
            }
        } catch (Exception e) {
            log.error("Failed to audit failed login: {}", e.getMessage());
        }
    }

    @AfterReturning(
            pointcut = "execution(* org.tricol.supplierchain.service.AuthServiceImpl.register(..))",
            returning = "result"
    )
    public void auditRegistration(JoinPoint joinPoint, RegisterResponse result) {
        try {
            Object[] args = joinPoint.getArgs();
            String ipAddress = getClientIpAddress();
            
            String username = "unknown";
            Long userId = null;
            
            // Extract username from RegisterRequest
            if (args.length > 0 && args[0] instanceof RegisterRequest) {
                RegisterRequest registerRequest = (RegisterRequest) args[0];
                username = registerRequest.getUsername();
                
                // Get userId from response data
                if (result.getData() != null) {
                    userId = result.getData().getId();
                }
            }

            auditService.logAudit(
                    userId,
                    username,
                    "REGISTER",
                    "AUTH",
                    "New user registered",
                    ipAddress
            );
        } catch (Exception e) {
            log.error("Failed to audit registration: {}", e.getMessage());
        }
    }



    @AfterReturning(
            pointcut = "execution(* org.tricol.supplierchain.service.AuthServiceImpl.refreshToken(..))",
            returning = "result"
    )
    public void auditTokenRefresh(JoinPoint joinPoint, LoginResponse result) {
        try {
            String ipAddress = getClientIpAddress();

            auditService.logAudit(
                    null,
                    "token-refresh",
                    "TOKEN_REFRESH",
                    "AUTH",
                    "Access token refreshed",
                    ipAddress
            );
        } catch (Exception e) {
            log.error("Failed to audit token refresh: {}", e.getMessage());
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
