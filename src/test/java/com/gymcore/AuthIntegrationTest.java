package com.gymcore;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gymcore.dto.LoginRequest;
import com.gymcore.dto.RegisterRequest;
import com.gymcore.entity.Role;
import com.gymcore.entity.SubscriptionPlan;
import com.gymcore.entity.Tenant;
import com.gymcore.repository.TenantRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

class AuthIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TenantRepository tenantRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Long tenantId;

    @BeforeEach
    void setUp() {
        Tenant tenant = new Tenant();
        tenant.setName("Test Gym");
        tenant.setSubdomain("test-gym");
        tenant.setContactEmail("test@gym.com");
        tenant.setSubscriptionPlan(SubscriptionPlan.PRO);
        tenant.setActive(true);
        tenantId = tenantRepository.save(tenant).getId();
    }

    @Test
    void shouldRegisterAndLoginSuccessfully() {
        // Register
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail("integration@test.com");
        registerRequest.setPassword("password123");
        registerRequest.setFullName("Integration Test");
        registerRequest.setRole(Role.GYM_MANAGER);
        registerRequest.setTenantId(tenantId);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<RegisterRequest> registerEntity =
                new HttpEntity<>(registerRequest, headers);

        ResponseEntity<String> registerResponse = restTemplate.postForEntity(
                "/api/auth/register", registerEntity, String.class);

        assertThat(registerResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(registerResponse.getBody()).contains("token");

        // Login
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("integration@test.com");
        loginRequest.setPassword("password123");

        HttpEntity<LoginRequest> loginEntity = new HttpEntity<>(loginRequest, headers);

        ResponseEntity<String> loginResponse = restTemplate.postForEntity(
                "/api/auth/login", loginEntity, String.class);

        assertThat(loginResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(loginResponse.getBody()).contains("token");
    }
}