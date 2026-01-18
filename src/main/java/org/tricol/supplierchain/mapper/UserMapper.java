package org.tricol.supplierchain.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.tricol.supplierchain.dto.request.RegisterRequest;
import org.tricol.supplierchain.dto.response.AuthResponse;
import org.tricol.supplierchain.entity.UserApp;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "userPermissions", ignore = true)
    @Mapping(target = "enabled", constant = "true")
    @Mapping(target = "locked", constant = "false")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    UserApp toEntity(RegisterRequest request);

    @Mapping(target = "accessToken", ignore = true)
    @Mapping(target = "tokenType", constant = "Bearer")
    @Mapping(target = "userId", source = "id")
    @Mapping(target = "role", expression = "java(user.getRole() != null ? user.getRole().getName().name() : null)")
    AuthResponse toAuthResponse(UserApp user);
}
