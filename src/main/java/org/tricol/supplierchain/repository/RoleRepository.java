package org.tricol.supplierchain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.tricol.supplierchain.entity.RoleApp;
import org.tricol.supplierchain.enums.RoleName;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<RoleApp, Long> {
    Optional<RoleApp> findByName(RoleName name);
}
