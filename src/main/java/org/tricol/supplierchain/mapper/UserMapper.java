package org.tricol.supplierchain.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.tricol.supplierchain.dto.response.UserResponseDto;
import org.tricol.supplierchain.entity.UserApp;

@Mapper(componentModel = "spring")
public interface UserMapper {
    
    @Mapping(source = "role.name", target = "roleName")
    UserResponseDto toResponse(UserApp user);
}
