package com.gymcore.service;

import com.gymcore.dto.AuthResponse;
import com.gymcore.dto.LoginRequest;
import com.gymcore.dto.RegisterRequest;
import com.gymcore.entity.Tenant;
import com.gymcore.entity.User;
import com.gymcore.repository.TenantRepository;
import com.gymcore.repository.UserRepository;
import com.gymcore.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final TenantRepository tenantRepository;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already in use");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setRole(request.getRole());

        Tenant tenant = tenantRepository.findById(request.getTenantId())
                .orElseThrow(() -> new RuntimeException("Tenant not found"));
        user.setTenant(tenant);

        userRepository.save(user);

        String token = jwtService.generateToken(
                user.getEmail(),
                user.getRole().name(),
                request.getTenantId()
        );

        return new AuthResponse(
                token,
                user.getEmail(),
                user.getRole().name(),
                request.getTenantId()
        );
    }

    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String token = jwtService.generateToken(
                user.getEmail(),
                user.getRole().name(),
                user.getTenant().getId()
        );

        return new AuthResponse(
                token,
                user.getEmail(),
                user.getRole().name(),
                user.getTenant().getId()
        );
    }
}