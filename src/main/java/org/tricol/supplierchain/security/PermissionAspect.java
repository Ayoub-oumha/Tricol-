package org.tricol.supplierchain.security;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.tricol.supplierchain.entity.UserApp;
import org.tricol.supplierchain.exception.InsufficientPermissionsException;
import org.tricol.supplierchain.repository.UserRepository;
import org.tricol.supplierchain.service.CurrentUserService;
import org.tricol.supplierchain.service.inter.PermissionService;

import java.lang.reflect.Method;

@Aspect
@Component
@RequiredArgsConstructor
public class PermissionAspect {

    private final PermissionService permissionService;
    private final CurrentUserService currentUserService;

    @Around("@annotation(org.tricol.supplierchain.security.RequirePermission)")
    public Object checkPermission(ProceedingJoinPoint joinPoint) throws Throwable {

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        RequirePermission requirePermission = method.getAnnotation(RequirePermission.class);
        String requiredPermission = requirePermission.value();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("User is not authenticated");
        }

        UserApp user = currentUserService.getCurrentUser();
        if (user == null) {
            throw new AccessDeniedException("User not found");
        }

        if (!permissionService.hasPermission(user, requiredPermission)) {
            throw new InsufficientPermissionsException("Access denied: missing required permissions");
        }
        return joinPoint.proceed();
    }
}
