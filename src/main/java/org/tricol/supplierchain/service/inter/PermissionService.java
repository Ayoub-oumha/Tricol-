package org.tricol.supplierchain.service.inter;

import org.tricol.supplierchain.entity.UserApp;

import java.util.Set;

public interface PermissionService {
    Set<String> getUserPermissions(UserApp user);
    boolean hasPermission(UserApp user, String permissionName);
}
