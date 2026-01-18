package org.tricol.supplierchain.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.tricol.supplierchain.dto.response.UserPermissionResponse;
import org.tricol.supplierchain.entity.UserPermission;

@Mapper(componentModel = "spring")
public interface UserPermissionMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "permissionId", source = "permission.id")
    @Mapping(target = "permissionName", source = "permission.name")
    UserPermissionResponse toResponse(UserPermission userPermission);
}
