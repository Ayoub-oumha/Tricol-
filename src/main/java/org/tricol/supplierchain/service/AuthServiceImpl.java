package org.tricol.supplierchain.service;

import lombok.RequiredArgsConstructor;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Cookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tricol.supplierchain.dto.request.LoginRequest;
import org.tricol.supplierchain.dto.request.RegisterRequest;
import org.tricol.supplierchain.dto.response.AuthResponse;
import org.tricol.supplierchain.entity.UserApp;
import org.tricol.supplierchain.enums.RoleName;
import org.tricol.supplierchain.exception.DuplicateResourceException;
import org.tricol.supplierchain.exception.OperationNotAllowedException;
import org.tricol.supplierchain.mapper.UserMapper;
import org.tricol.supplierchain.repository.RoleRepository;
import org.tricol.supplierchain.repository.UserRepository;
import org.tricol.supplierchain.security.JwtService;
import org.tricol.supplierchain.security.CustomUserDetailsService;
import org.tricol.supplierchain.service.inter.AuditService;
import org.tricol.supplierchain.service.inter.AuthService;


@Service
@RequiredArgsConstructor
public class  AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserMapper userMapper;
    private final CustomUserDetailsService userDetailsService;
    private final AuditService auditService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already exists");
        }

        UserApp user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        if (userRepository.count() == 0) {
            roleRepository.findByName(RoleName.ADMIN)
                    .ifPresent(user::setRole);
        }

        user = userRepository.save(user);

        auditService.logWithUser(
                user.getId(),
                user.getUsername(),
                "REGISTER",
                "USER",
                user.getId().toString(),
                "New user registered"
        );

        return userMapper.toAuthResponse(user);
    }

    @Override
    public AuthResponse login(LoginRequest request, HttpServletResponse response) {

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            UserApp user = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow();

            if (user.getRole() == null) {
                auditService.logAuthentication(request.getUsername(), "NOT_HAVE_ROLE", false);
                throw new OperationNotAllowedException("User does not have an assigned role");
            }

            String accessToken = jwtService.generateToken(userDetails);
            String refreshToken = jwtService.generateRefreshToken(userDetails);

            Cookie cookie = new Cookie("refreshToken", refreshToken);
            cookie.setHttpOnly(true);
            cookie.setSecure(false);
            cookie.setPath("/api/auth/refresh");
            cookie.setMaxAge((int) jwtService.getRefreshTokenExpirationInSeconds());

            response.addCookie(cookie);

            auditService.logAuthentication(user.getUsername(), "LOGIN_SUCCESS", true);

            AuthResponse authResponse = userMapper.toAuthResponse(user);
            authResponse.setAccessToken(accessToken);

            return authResponse;

        } catch (BadCredentialsException e) {
            auditService.logAuthentication(request.getUsername(), "LOGIN_FAILURE", false);
            throw e;
        }
    }
}
