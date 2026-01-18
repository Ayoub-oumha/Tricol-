package org.tricol.supplierchain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tricol.supplierchain.entity.RolePermission;
import org.tricol.supplierchain.entity.UserApp;
import org.tricol.supplierchain.entity.UserPermission;
import org.tricol.supplierchain.repository.RolePermissionRepository;
import org.tricol.supplierchain.repository.UserPermissionRepository;
import org.tricol.supplierchain.service.inter.PermissionService;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PermissionServiceImpl implements PermissionService {

    private final RolePermissionRepository rolePermissionRepository;
    private final UserPermissionRepository userPermissionRepository;

    @Override
    @Transactional(readOnly = true)
    public Set<String> getUserPermissions(UserApp user) {

        Set<String> permissions = new HashSet<>();

        if (user.getRole() != null) {
            List<RolePermission> rolePermissions = rolePermissionRepository
                    .findByRoleWithPermissions(user.getRole());
            
            permissions = rolePermissions
                    .stream()
                    .map(rp -> rp.getPermission().getName())
                    .collect(Collectors.toSet());
        }


        List<UserPermission> userPermissions = userPermissionRepository
                .findByUserWithPermissions(user);


        for (UserPermission up : userPermissions) {
            if (Boolean.TRUE.equals(up.getGranted())) {
                permissions.add(up.getPermission().getName());
            } else {
                permissions.remove(up.getPermission().getName());
            }
        }

        return permissions;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasPermission(UserApp user, String permissionName) {
        Set<String> userPermissions = getUserPermissions(user);
        return userPermissions.contains(permissionName);
    }
}
