package org.tricol.supplierchain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.tricol.supplierchain.entity.UserApp;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserApp, Long>{
    Optional<UserApp> findByUsername(String username);
    Optional<UserApp> findByKeycloakUserId(String keycloakUserId);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
