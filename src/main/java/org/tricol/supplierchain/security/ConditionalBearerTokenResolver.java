package org.tricol.supplierchain.security;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;
import org.springframework.stereotype.Component;

@Component
public class ConditionalBearerTokenResolver implements BearerTokenResolver {

    private final DefaultBearerTokenResolver defaultResolver = new DefaultBearerTokenResolver();

    @Override
    public String resolve(HttpServletRequest request) {
        if (SecurityContextHolder.getContext().getAuthentication() != null 
                && SecurityContextHolder.getContext().getAuthentication().isAuthenticated()) {
            return null;
        }
        return defaultResolver.resolve(request);
    }
}
