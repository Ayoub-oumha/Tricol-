package org.tricol.supplierchain.service.inter;


public interface AuditService {

    void log(String action, String resource, String resourceId, String details);

    void logWithUser(Long userId, String username, String action, String resource, String resourceId, String details);

    void logAuthentication(String username, String action, boolean success);

    void logPermissionChange(Long userId, String username, String permissionName, boolean granted, Long grantedBy);

}

