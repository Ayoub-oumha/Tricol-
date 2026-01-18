package org.tricol.supplierchain.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.tricol.supplierchain.dto.request.UserPermissionRequest;
import org.tricol.supplierchain.dto.response.UserPermissionResponse;
import org.tricol.supplierchain.security.CustomUserDetails;
import org.tricol.supplierchain.service.inter.UserManagementService;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class UserManagementController {

    private final UserManagementService userManagementService;

    @PostMapping("/permissions")
    @PreAuthorize("hasAuthority('USER_MANAGE')")
    public ResponseEntity<UserPermissionResponse> assignPermission(@RequestBody UserPermissionRequest request, Authentication authentication) {
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        UserPermissionResponse response = userManagementService.assignPermissionToUser(request, userDetails.getUser().getId());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{userId}/permissions/{permissionId}")
    @PreAuthorize("hasAuthority('USER_MANAGE')")
    public ResponseEntity<String> removePermission(@PathVariable Long userId, @PathVariable Long permissionId) {
        userManagementService.removePermissionFromUser(userId, permissionId);
        return ResponseEntity.ok("Permission removed successfully");
    }

    @PatchMapping("/{userId}/permissions/{permissionId}/activate")
    @PreAuthorize("hasAuthority('USER_MANAGE')")
    public ResponseEntity<String> activatePermission(@PathVariable Long userId, @PathVariable Long permissionId) {
        userManagementService.activatePermission(userId, permissionId);
        return ResponseEntity.ok("Permission activated successfully");
    }

    @PatchMapping("/{userId}/permissions/{permissionId}/deactivate")
    @PreAuthorize("hasAuthority('USER_MANAGE')")
    public ResponseEntity<String> deactivatePermission(@PathVariable Long userId, @PathVariable Long permissionId) {
        userManagementService.deactivatePermission(userId, permissionId);
        return ResponseEntity.ok("Permission deactivated successfully");
    }

    @PostMapping("/{userId}/role/{roleId}")
    @PreAuthorize("hasAuthority('USER_MANAGE')")
    public ResponseEntity<String> assignRole(@PathVariable Long userId, @PathVariable Long roleId) {
        userManagementService.assignRoleToUser(userId, roleId);
        return ResponseEntity.ok("Role assigned successfully");
    }
}
