package org.tricol.supplierchain.service.inter;

import jakarta.servlet.http.HttpServletResponse;
import org.tricol.supplierchain.dto.request.LoginRequest;
import org.tricol.supplierchain.dto.request.RegisterRequest;
import org.tricol.supplierchain.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request, HttpServletResponse response);
}
