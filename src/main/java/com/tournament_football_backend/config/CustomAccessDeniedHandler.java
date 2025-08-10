package com.tournament_football_backend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
/*
* CustomAccessDeniedHandler.java
* This class implements the AccessDeniedHandler interface to handle access denied errors.
* It is invoked when a user tries to access a resource they do not have permission for.
* It logs the error and sends a JSON response with details about the access denied error.
* It sets the response status to 403 Forbidden and includes an error message in the response body.
 */
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private static final Logger logger = LoggerFactory.getLogger(CustomAccessDeniedHandler.class);

    // This method is invoked when a user tries to access a resource they do not have permission for
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {

        logger.warn("Access denied: {} for user accessing {}",
                accessDeniedException.getMessage(), request.getServletPath());

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        final Map<String, Object> body = new HashMap<>();
        body.put("error", "ACCESS_DENIED");
        body.put("message", "Non hai i permessi per accedere a questa risorsa");
        body.put("status", HttpServletResponse.SC_FORBIDDEN);
        body.put("path", request.getServletPath());

        final ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), body);
    }
}