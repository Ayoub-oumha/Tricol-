package org.tricol.supplierchain.service.inter;

import org.tricol.supplierchain.dto.request.KeycloakLoginRequest;
import org.tricol.supplierchain.dto.request.KeycloakRegisterRequest;
import org.tricol.supplierchain.dto.response.KeycloakRegisterResponse;
import org.tricol.supplierchain.dto.response.KeycloakTokenResponse;

public interface KeycloakService {

    KeycloakRegisterResponse registerUser(KeycloakRegisterRequest request);

    KeycloakTokenResponse login(KeycloakLoginRequest request);

    KeycloakTokenResponse refreshToken(String refreshToken);

    void logout(String refreshToken);
}

