package org.tricol.supplierchain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.tricol.supplierchain.entity.UserApp;
import org.tricol.supplierchain.entity.UserPermission;

import java.util.List;


@Repository
public interface UserPermissionRepository extends JpaRepository<UserPermission, Long> {

    @Query("SELECT up FROM UserPermission up JOIN FETCH up.permission WHERE up.user = :user")
    List<UserPermission> findByUserWithPermissions(@Param("user") UserApp user);

}
