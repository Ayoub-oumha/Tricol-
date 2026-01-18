package org.tricol.supplierchain.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.tricol.supplierchain.dto.request.LoginRequest;
import org.tricol.supplierchain.dto.request.RegisterRequest;
import org.tricol.supplierchain.dto.response.AuthResponse;
import org.tricol.supplierchain.entity.RoleApp;
import org.tricol.supplierchain.entity.UserApp;
import org.tricol.supplierchain.enums.RoleName;
import org.tricol.supplierchain.exception.DuplicateResourceException;
import org.tricol.supplierchain.exception.OperationNotAllowedException;
import org.tricol.supplierchain.mapper.UserMapper;
import org.tricol.supplierchain.repository.RoleRepository;
import org.tricol.supplierchain.repository.UserRepository;
import org.tricol.supplierchain.security.CustomUserDetailsService;
import org.tricol.supplierchain.security.JwtService;
import org.tricol.supplierchain.service.inter.AuditService;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests unitaires pour AuthService")
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserMapper userMapper;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private AuditService auditService;

    @Mock
    private HttpServletResponse httpServletResponse;

    @InjectMocks
    private AuthServiceImpl authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private UserApp userApp;
    private RoleApp roleApp;
    private UserDetails userDetails;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setUsername("testuser");
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setFullName("Test User");

        loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");

        roleApp = new RoleApp();
        roleApp.setId(1L);
        roleApp.setName(RoleName.MAGASINIER);

        userApp = new UserApp();
        userApp.setId(1L);
        userApp.setUsername("testuser");
        userApp.setEmail("test@example.com");
        userApp.setPassword("encodedPassword");
        userApp.setFullName("Test User");
        userApp.setRole(roleApp);
        userApp.setEnabled(true);

        userDetails = User.builder()
                .username("testuser")
                .password("encodedPassword")
                .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_MAGASINIER")))
                .build();
    }

    @Test
    void register_ShouldCreateNewUser_WhenValidRequest() {
        when(userRepository.existsByUsername(registerRequest.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(userRepository.count()).thenReturn(1L); // Pas le premier utilisateur
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encodedPassword");
        when(userMapper.toEntity(registerRequest)).thenReturn(userApp);
        when(userRepository.save(any(UserApp.class))).thenReturn(userApp);

        AuthResponse expectedResponse = AuthResponse.builder()
                .userId(1L)
                .username("testuser")
                .email("test@example.com")
                .role("MAGASINIER")
                .build();

        when(userMapper.toAuthResponse(userApp)).thenReturn(expectedResponse);

        AuthResponse response = authService.register(registerRequest);

        assertThat(response).isNotNull();
        assertThat(response.getUsername()).isEqualTo("testuser");
        assertThat(response.getEmail()).isEqualTo("test@example.com");

        verify(userRepository).existsByUsername(registerRequest.getUsername());
        verify(userRepository).existsByEmail(registerRequest.getEmail());
        verify(passwordEncoder).encode(registerRequest.getPassword());
        verify(userRepository).save(any(UserApp.class));
        verify(auditService).logWithUser(eq(1L), eq("testuser"), eq("REGISTER"), eq("USER"),
                eq("1"), eq("New user registered"));
    }

    @Test
    @DisplayName("Register - Devrait assigner le rôle ADMIN au premier utilisateur")
    void register_ShouldAssignAdminRole_WhenFirstUser() {

        when(userRepository.existsByUsername(registerRequest.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(false);
        when(userRepository.count()).thenReturn(0L); // Premier utilisateur
        when(passwordEncoder.encode(registerRequest.getPassword())).thenReturn("encodedPassword");
        when(userMapper.toEntity(registerRequest)).thenReturn(userApp);

        RoleApp adminRole = new RoleApp();
        adminRole.setId(1L);
        adminRole.setName(RoleName.ADMIN);
        when(roleRepository.findByName(RoleName.ADMIN)).thenReturn(Optional.of(adminRole));

        when(userRepository.save(any(UserApp.class))).thenReturn(userApp);

        AuthResponse expectedResponse = AuthResponse.builder()
                .userId(1L)
                .username("testuser")
                .email("test@example.com")
                .role("ADMIN")
                .build();

        when(userMapper.toAuthResponse(any(UserApp.class))).thenReturn(expectedResponse);

        AuthResponse response = authService.register(registerRequest);

        assertThat(response).isNotNull();
        assertThat(response.getRole()).isEqualTo("ADMIN");
        verify(userRepository).count();
        verify(roleRepository).findByName(RoleName.ADMIN);
    }

    @Test
    @DisplayName("Register - Devrait échouer quand le username existe déjà")
    void register_ShouldThrowException_WhenUsernameExists() {
        when(userRepository.existsByUsername(registerRequest.getUsername())).thenReturn(true);

        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("Username already exists");

        verify(userRepository).existsByUsername(registerRequest.getUsername());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Register - Devrait échouer quand l'email existe déjà")
    void register_ShouldThrowException_WhenEmailExists() {
        when(userRepository.existsByUsername(registerRequest.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(registerRequest.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("Email already exists");

        verify(userRepository).existsByUsername(registerRequest.getUsername());
        verify(userRepository).existsByEmail(registerRequest.getEmail());
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Login - Devrait authentifier l'utilisateur avec succès")
    void login_ShouldAuthenticateUser_WhenCredentialsValid() {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(userApp));
        when(jwtService.generateToken(userDetails)).thenReturn("accessToken");
        when(jwtService.generateRefreshToken(userDetails)).thenReturn("refreshToken");

        AuthResponse expectedResponse = AuthResponse.builder()
                .userId(1L)
                .username("testuser")
                .email("test@example.com")
                .role("MAGASINIER")
                .accessToken("accessToken")
                .tokenType("Bearer")
                .build();

        when(userMapper.toAuthResponse(userApp)).thenReturn(expectedResponse);

        AuthResponse response = authService.login(loginRequest, httpServletResponse);

        assertThat(response).isNotNull();
        assertThat(response.getUsername()).isEqualTo("testuser");
        assertThat(response.getAccessToken()).isEqualTo("accessToken");

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository).findByUsername("testuser");
        verify(jwtService).generateToken(userDetails);
        verify(jwtService).generateRefreshToken(userDetails);
        verify(httpServletResponse).addCookie(any(Cookie.class));
        verify(auditService).logAuthentication("testuser", "LOGIN_SUCCESS", true);
    }

    @Test
    @DisplayName("Login - Devrait échouer quand les credentials sont invalides")
    void login_ShouldThrowException_WhenCredentialsInvalid() {

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThatThrownBy(() -> authService.login(loginRequest, httpServletResponse))
                .isInstanceOf(BadCredentialsException.class);

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(auditService).logAuthentication("testuser", "LOGIN_FAILURE", false);
        verify(jwtService, never()).generateToken(any());
    }

    @Test
    void login_ShouldThrowException_WhenUserHasNoRole() {
        userApp.setRole(null);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(userDetails);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(userApp));

        assertThatThrownBy(() -> authService.login(loginRequest, httpServletResponse))
                .isInstanceOf(OperationNotAllowedException.class)
                .hasMessage("User does not have an assigned role");

        verify(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(userRepository).findByUsername("testuser");
        verify(auditService).logAuthentication("testuser", "NOT_HAVE_ROLE", false);
        verify(jwtService, never()).generateToken(any());
    }
}
