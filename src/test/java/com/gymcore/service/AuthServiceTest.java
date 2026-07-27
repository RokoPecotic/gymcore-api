package com.gymcore.service;

import com.gymcore.dto.AuthResponse;
import com.gymcore.dto.LoginRequest;
import com.gymcore.dto.RegisterRequest;
import com.gymcore.entity.Role;
import com.gymcore.entity.Tenant;
import com.gymcore.entity.User;
import com.gymcore.repository.TenantRepository;
import com.gymcore.repository.UserRepository;
import com.gymcore.security.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private TenantRepository tenantRepository;

    @InjectMocks
    private AuthService authService;

    private Tenant tenant;
    private User user;
    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        tenant = new Tenant();
        tenant.setId(1L);
        tenant.setName("GymCore");

        user = new User();
        user.setId(1L);
        user.setEmail("roko@gymcore.com");
        user.setPassword("hashedPassword");
        user.setFullName("Roko Pecotic");
        user.setRole(Role.GYM_MANAGER);
        user.setTenant(tenant);
        user.setActive(true);

        registerRequest = new RegisterRequest();
        registerRequest.setEmail("roko@gymcore.com");
        registerRequest.setPassword("lozinka123");
        registerRequest.setFullName("Roko Pecotic");
        registerRequest.setRole(Role.GYM_MANAGER);
        registerRequest.setTenantId(1L);

        loginRequest = new LoginRequest();
        loginRequest.setEmail("roko@gymcore.com");
        loginRequest.setPassword("lozinka123");
    }

    @Test
    @DisplayName("Should register user when email is not taken")
    void shouldRegisterUser_whenEmailIsNotTaken() {
        when(userRepository.existsByEmail("roko@gymcore.com")).thenReturn(false);
        when(tenantRepository.findById(1L)).thenReturn(Optional.of(tenant));
        when(passwordEncoder.encode("lozinka123")).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(jwtService.generateToken(anyString(), anyString(), any()))
                .thenReturn("fake-jwt-token");

        AuthResponse result = authService.register(registerRequest);

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("roko@gymcore.com");
        assertThat(result.getRole()).isEqualTo("GYM_MANAGER");
        assertThat(result.getToken()).isEqualTo("fake-jwt-token");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw exception when email already in use")
    void shouldThrowException_whenEmailAlreadyInUse() {
        when(userRepository.existsByEmail("roko@gymcore.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(registerRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Email already in use");

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should login user when credentials are valid")
    void shouldLoginUser_whenCredentialsAreValid() {
        when(userRepository.findByEmail("roko@gymcore.com"))
                .thenReturn(Optional.of(user));
        when(jwtService.generateToken(anyString(), anyString(), any()))
                .thenReturn("fake-jwt-token");

        AuthResponse result = authService.login(loginRequest);

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo("roko@gymcore.com");
        assertThat(result.getToken()).isEqualTo("fake-jwt-token");
    }

    @Test
    @DisplayName("Should throw exception when user not found during login")
    void shouldThrowException_whenUserNotFoundDuringLogin() {
        when(userRepository.findByEmail("roko@gymcore.com"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found");
    }
}
