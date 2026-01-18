package org.tricol.supplierchain.service.inter;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.tricol.supplierchain.dto.request.AssignRoleRequest;
import org.tricol.supplierchain.dto.request.ModifyPermissionRequest;
import org.tricol.supplierchain.dto.response.UserResponseDto;
import org.tricol.supplierchain.dto.response.UserWithPermissionsResponseDto;

import java.util.Set;

public interface UserAdminService {
    UserWithPermissionsResponseDto assignRole(Long userId, AssignRoleRequest request);
    UserWithPermissionsResponseDto modifyPermission(Long userId, ModifyPermissionRequest request);
    Set<String> getUserEffectivePermissions(Long userId);
    Page<UserResponseDto> getUsers(Pageable pageable);
}
