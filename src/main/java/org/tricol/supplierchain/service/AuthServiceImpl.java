package org.tricol.supplierchain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tricol.supplierchain.dto.request.LoginRequest;
import org.tricol.supplierchain.dto.request.RegisterRequest;
import org.tricol.supplierchain.dto.response.LoginResponse;
import org.tricol.supplierchain.dto.response.RegisterResponse;
import org.tricol.supplierchain.entity.RefreshToken;
import org.tricol.supplierchain.entity.Role;
import org.tricol.supplierchain.entity.UserApp;
import org.tricol.supplierchain.exception.BusinessException;
import org.tricol.supplierchain.exception.DuplicateResourceException;
import org.tricol.supplierchain.mapper.UserMapper;
import org.tricol.supplierchain.repository.RoleRepository;
import org.tricol.supplierchain.repository.UserRepository;
import org.tricol.supplierchain.security.CustomUserDetailsService;
import org.tricol.supplierchain.security.JwtService;
import org.tricol.supplierchain.service.inter.AuthService;
import org.tricol.supplierchain.service.inter.RefreshTokenService;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;
    private final CurrentUserService currentUserService;

    @Override
    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateResourceException("Username already exists: " + request.getUsername());
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email already exists: " + request.getEmail());
        }
        Role role = null;
        if (userRepository.count() == 0){
            role = roleRepository.findByName("ADMIN").orElse(null);
        }

        UserApp user = UserApp.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(role)
                .enabled(true)
                .build();

        userRepository.save(user);

        return RegisterResponse
                .builder()
                .status("SUCCESS")
                .message("Your account has been created successfully, waiting for admin approval")
                .code("USER_REGISTERED_PENDING_ROLE")
                .data(userMapper.toResponse(user))
                .build();
    }

    @Override
    @Transactional
    public LoginResponse login(LoginRequest request) {

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        UserApp user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BusinessException("User not found"));

        if (user.getRole() == null) {
            throw new BusinessException("User has no role assigned. Please contact administrator.");
        }

        String accessToken = jwtService.generateAccessToken(userDetails);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);

        //currentUserService.getCurrentUser();

        return LoginResponse
                .builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .build();
    }

    @Override
    @Transactional
    public LoginResponse refreshToken(String token) {

        RefreshToken newRefreshToken = refreshTokenService.validateAndRotate(token);
        UserApp user = newRefreshToken.getUser();


        UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
        String accessToken = jwtService.generateAccessToken(userDetails);

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(newRefreshToken.getToken())
                .build();
    }

}
