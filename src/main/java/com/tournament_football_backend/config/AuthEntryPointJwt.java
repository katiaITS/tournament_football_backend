package com.tournament_football_backend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
/*
    * AuthEntryPointJwt.java
    * This class implements the AuthenticationEntryPoint interface to handle authentication errors.
    * It is invoked when an unauthenticated user tries to access a protected resource.
    * It logs the error and sends a JSON response with details about the authentication failure.
    * It sets the response status to 401 Unauthorized and includes an error message in the response body.
 */
@Component
public class AuthEntryPointJwt implements AuthenticationEntryPoint {
    // This class handles authentication errors and sends a JSON response
    private static final Logger logger = LoggerFactory.getLogger(AuthEntryPointJwt.class);

    // This method is invoked when an unauthenticated user tries to access a protected resource
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        // Log the authentication error
        logger.warn("Authentication error: {} for request to {}",
                authException.getMessage(), request.getServletPath());

        // Set the response content type to JSON
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        // Create a response body with error details
        final Map<String, Object> body = new HashMap<>();
        body.put("error", "AUTHENTICATION_FAILED");
        body.put("message", "Token JWT non valido o mancante");
        body.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        body.put("path", request.getServletPath());

        // Write the response body as JSON
        final ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), body);
    }
}