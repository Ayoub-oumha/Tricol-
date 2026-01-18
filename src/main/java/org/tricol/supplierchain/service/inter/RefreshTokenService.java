package org.tricol.supplierchain.service.inter;

import org.tricol.supplierchain.entity.RefreshToken;
import org.tricol.supplierchain.entity.UserApp;

public interface RefreshTokenService {
    RefreshToken createRefreshToken(UserApp user);
    RefreshToken validateAndRotate(String token);
    void revokeToken(String token);
}
