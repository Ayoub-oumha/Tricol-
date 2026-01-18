package org.tricol.supplierchain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tricol.supplierchain.entity.UserPermission;

import java.util.Optional;

public interface UserPermissionRepository extends JpaRepository<UserPermission, Long> {
    Optional<UserPermission> findByUserIdAndPermissionId(Long userId, Long permissionId);
}
