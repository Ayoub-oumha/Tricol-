package org.tricol.supplierchain.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KeycloakRegisterResponse {
    private String status;
    private String message;
    private String userId;
    private String username;
    private String email;
}
