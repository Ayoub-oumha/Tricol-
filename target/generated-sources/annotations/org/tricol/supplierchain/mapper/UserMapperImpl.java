package org.tricol.supplierchain.mapper;

import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;
import org.tricol.supplierchain.dto.request.RegisterRequest;
import org.tricol.supplierchain.dto.response.AuthResponse;
import org.tricol.supplierchain.entity.UserApp;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-01-18T17:46:12+0100",
    comments = "version: 1.6.3, compiler: javac, environment: Java 17.0.17 (Eclipse Adoptium)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Override
    public UserApp toEntity(RegisterRequest request) {
        if ( request == null ) {
            return null;
        }

        UserApp.UserAppBuilder userApp = UserApp.builder();

        userApp.username( request.getUsername() );
        userApp.email( request.getEmail() );
        userApp.fullName( request.getFullName() );

        userApp.enabled( true );
        userApp.locked( false );

        return userApp.build();
    }

    @Override
    public AuthResponse toAuthResponse(UserApp user) {
        if ( user == null ) {
            return null;
        }

        AuthResponse.AuthResponseBuilder authResponse = AuthResponse.builder();

        authResponse.userId( user.getId() );
        authResponse.username( user.getUsername() );
        authResponse.email( user.getEmail() );

        authResponse.tokenType( "Bearer" );
        authResponse.role( user.getRole() != null ? user.getRole().getName().name() : null );

        return authResponse.build();
    }
}
