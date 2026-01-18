package org.tricol.supplierchain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.tricol.supplierchain.entity.UserApp;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserApp, Long> {
    @Query("SELECT u FROM UserApp u " +
           "LEFT JOIN FETCH u.role r " +
           "LEFT JOIN FETCH r.permissions " +
           "LEFT JOIN FETCH u.userPermissions up " +
           "LEFT JOIN FETCH up.permission " +
           "WHERE u.username = :username")
    Optional<UserApp> findByUsername(@Param("username") String username);
    Optional<UserApp> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
