package org.tricol.supplierchain.service.inter;

import org.tricol.supplierchain.dto.request.UserPermissionRequest;
import org.tricol.supplierchain.dto.response.UserPermissionResponse;

public interface UserManagementService {
    UserPermissionResponse assignPermissionToUser(UserPermissionRequest request, Long adminId);
    void removePermissionFromUser(Long userId, Long permissionId);
    void activatePermission(Long userId, Long permissionId);
    void deactivatePermission(Long userId, Long permissionId);
    void assignRoleToUser(Long userId, Long roleId);
}
