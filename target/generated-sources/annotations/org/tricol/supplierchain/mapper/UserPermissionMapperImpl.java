package org.tricol.supplierchain.mapper;

import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import org.tricol.supplierchain.dto.response.UserPermissionResponse;
import org.tricol.supplierchain.entity.Permission;
import org.tricol.supplierchain.entity.UserApp;
import org.tricol.supplierchain.entity.UserPermission;
import org.tricol.supplierchain.enums.PermissionName;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-01-18T17:46:11+0100",
    comments = "version: 1.6.3, compiler: javac, environment: Java 17.0.17 (Eclipse Adoptium)"
)
@Component
public class UserPermissionMapperImpl implements UserPermissionMapper {

    @Override
    public UserPermissionResponse toResponse(UserPermission userPermission) {
        if ( userPermission == null ) {
            return null;
        }

        UserPermissionResponse userPermissionResponse = new UserPermissionResponse();

        userPermissionResponse.setUserId( userPermissionUserId( userPermission ) );
        userPermissionResponse.setUsername( userPermissionUserUsername( userPermission ) );
        userPermissionResponse.setPermissionId( userPermissionPermissionId( userPermission ) );
        PermissionName name = userPermissionPermissionName( userPermission );
        if ( name != null ) {
            userPermissionResponse.setPermissionName( name.name() );
        }
        userPermissionResponse.setId( userPermission.getId() );
        userPermissionResponse.setActive( userPermission.isActive() );
        userPermissionResponse.setGrantedBy( userPermission.getGrantedBy() );
        userPermissionResponse.setGrantedAt( userPermission.getGrantedAt() );
        userPermissionResponse.setRevokedAt( userPermission.getRevokedAt() );

        return userPermissionResponse;
    }

    private Long userPermissionUserId(UserPermission userPermission) {
        UserApp user = userPermission.getUser();
        if ( user == null ) {
            return null;
        }
        return user.getId();
    }

    private String userPermissionUserUsername(UserPermission userPermission) {
        UserApp user = userPermission.getUser();
        if ( user == null ) {
            return null;
        }
        return user.getUsername();
    }

    private Long userPermissionPermissionId(UserPermission userPermission) {
        Permission permission = userPermission.getPermission();
        if ( permission == null ) {
            return null;
        }
        return permission.getId();
    }

    private PermissionName userPermissionPermissionName(UserPermission userPermission) {
        Permission permission = userPermission.getPermission();
        if ( permission == null ) {
            return null;
        }
        return permission.getName();
    }
}
