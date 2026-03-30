package com.scientia.mercatus.test;

import com.scientia.mercatus.util.JwtTestUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

/**
 * Base class for integration tests that require security enabled.
 * 
 * Features:
 * - Security enabled (app.security.disabled=false)
 * - JWT tokens automatically generated and injected
 * - Test users (USER and ADMIN roles) available
 * - Existing test code stays unchanged - just extend this class
 * 
 * Usage:
 * <pre>
 * @SpringBootTest
 * @AutoConfigureMockMvc
 * public class YourTest extends BaseIntegrationTest {
 *     // Your existing test code stays exactly the same
 *     // Just extend this class
 * }
 * </pre>
 */
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public abstract class BaseIntegrationTest {
    
    @Autowired
    protected MockMvc mockMvc;
    
    @Autowired
    protected JwtTestUtil jwtTestUtil;
    
    // Test user credentials
    protected static final String TEST_USER_EMAIL = "testuser@test.com";
    protected static final String TEST_ADMIN_EMAIL = "testadmin@test.com";
    
    /**
     * Generate a valid JWT token for a test user with USER role.
     * Use this in MockMvc requests like:
     * 
     * mockMvc.perform(get("/api/v1/protected")
     *     .header("Authorization", "Bearer " + generateUserToken()))
     * 
     * @return JWT token string
     */
    protected String generateUserToken() {
        return generateUserToken(TEST_USER_EMAIL);
    }
    
    /**
     * Generate a JWT token with USER role for specified email.
     * 
     * @param email User's email
     * @return JWT token string
     */
    protected String generateUserToken(String email) {
        return jwtTestUtil.generateUserToken(email);
    }
    
    /**
     * Generate a valid JWT token for a test admin with ADMIN role.
     * 
     * @return JWT token string
     */
    protected String generateAdminToken() {
        return generateAdminToken(TEST_ADMIN_EMAIL);
    }
    
    /**
     * Generate a JWT token with ADMIN role for specified email.
     * 
     * @param email Admin's email
     * @return JWT token string
     */
    protected String generateAdminToken(String email) {
        return jwtTestUtil.generateAdminToken(email);
    }
    
    /**
     * Generate an expired JWT token for testing token validation/rejection.
     * 
     * @return Expired JWT token string
     */
    protected String generateExpiredToken() {
        return generateExpiredToken(TEST_USER_EMAIL);
    }
    
    /**
     * Generate an expired JWT token for specified email.
     * 
     * @param email User's email
     * @return Expired JWT token string
     */
    protected String generateExpiredToken(String email) {
        return jwtTestUtil.generateExpiredToken(email);
    }
    
    /**
     * Generate a JWT token with custom role.
     * 
     * @param email User's email
     * @param role The role to assign (e.g., "ROLE_USER", "ROLE_ADMIN")
     * @return JWT token string
     */
    protected String generateTokenWithRole(String email, String role) {
        return jwtTestUtil.generateTokenWithRole(email, role);
    }
    
    /**
     * Add an Authorization header with a Bearer token to a request.
     * Convenience method to reduce boilerplate.
     * 
     * Usage:
     * mockMvc.perform(addAuthToken(get("/api/v1/protected"), generateUserToken()))
     * 
     * @param builder The request builder
     * @param token The JWT token
     * @return The modified request builder
     */
    protected MockHttpServletRequestBuilder addAuthToken(
            MockHttpServletRequestBuilder builder, 
            String token) {
        return builder.header("Authorization", "Bearer " + token);
    }
}


