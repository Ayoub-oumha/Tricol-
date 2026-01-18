package org.tricol.supplierchain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tricol.supplierchain.dto.request.AssignRoleRequest;
import org.tricol.supplierchain.dto.request.ModifyPermissionRequest;
import org.tricol.supplierchain.dto.response.UserResponseDto;
import org.tricol.supplierchain.dto.response.UserWithPermissionsResponseDto;
import org.tricol.supplierchain.entity.Permission;
import org.tricol.supplierchain.entity.Role;
import org.tricol.supplierchain.entity.UserApp;
import org.tricol.supplierchain.entity.UserPermission;
import org.tricol.supplierchain.exception.ResourceNotFoundException;
import org.tricol.supplierchain.mapper.UserMapper;
import org.tricol.supplierchain.repository.PermissionRepository;
import org.tricol.supplierchain.repository.RoleRepository;
import org.tricol.supplierchain.repository.UserPermissionRepository;
import org.tricol.supplierchain.repository.UserRepository;
import org.tricol.supplierchain.service.inter.PermissionService;
import org.tricol.supplierchain.service.inter.UserAdminService;

import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserAdminServiceImpl implements UserAdminService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final UserPermissionRepository userPermissionRepository;
    private final PermissionService permissionService;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserWithPermissionsResponseDto assignRole(Long userId, AssignRoleRequest request) {
        UserApp user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        
        Role role = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException("Invalid Role"));
        
        user.setRole(role);
        userRepository.save(user);

        Set<String> effectivePermissions = permissionService.getUserPermissions(user);

        return UserWithPermissionsResponseDto
                .builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .roleName(role.getName())
                .enabled(user.getEnabled())
                .effectivePermissions(effectivePermissions)
                .build();
    }

    @Override
    @Transactional
    public UserWithPermissionsResponseDto modifyPermission(Long userId, ModifyPermissionRequest request) {
        UserApp user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Permission permission = permissionRepository.findById(request.getPermissionId())
                .orElseThrow(() -> new ResourceNotFoundException("Invalid Permission"));

        String modifiedBy = SecurityContextHolder.getContext().getAuthentication().getName();

        Optional<UserPermission> existingPermission = userPermissionRepository
                .findByUserWithPermissions(user)
                .stream()
                .filter(up -> up.getPermission().getId().equals(permission.getId()))
                .findFirst();

        if (existingPermission.isPresent()) {
            UserPermission up = existingPermission.get();
            up.setGranted(request.getGranted());
            up.setModifiedBy(modifiedBy);
            userPermissionRepository.save(up);
        } else {
            UserPermission up = UserPermission
                    .builder()
                    .user(user)
                    .permission(permission)
                    .granted(request.getGranted())
                    .modifiedBy(modifiedBy)
                    .build();
            userPermissionRepository.save(up);
        }

        Set<String> effectivePermissions = permissionService.getUserPermissions(user);

        return UserWithPermissionsResponseDto
                .builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .roleName(user.getRole() != null ? user.getRole().getName() : null)
                .enabled(user.getEnabled())
                .effectivePermissions(effectivePermissions)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Set<String> getUserEffectivePermissions(Long userId) {
        UserApp user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return permissionService.getUserPermissions(user);
    }

    @Override
    public Page<UserResponseDto> getUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(userMapper::toResponse);
    }
}
