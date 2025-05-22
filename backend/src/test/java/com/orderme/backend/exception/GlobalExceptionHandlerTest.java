package com.orderme.backend.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.ServletWebRequest;
import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();
    private final MockHttpServletRequest request = new MockHttpServletRequest();
    private final ServletWebRequest webRequest = new ServletWebRequest(request);

    @Test
    void handleResourceNotFoundException_ShouldReturnNotFoundResponse() {
        // Arrange
        ResourceNotFoundException ex = new ResourceNotFoundException("Resource not found");

        // Act
        ResponseEntity<ErrorResponse> response = handler.handleResourceNotFoundException(ex, webRequest);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getBody().getStatus());
        assertEquals("Resource not found", response.getBody().getMessage());
    }

    @Test
    void handleGlobalException_ShouldReturnInternalServerErrorResponse() {
        // Arrange
        Exception ex = new RuntimeException("Unexpected error");

        // Act
        ResponseEntity<ErrorResponse> response = handler.handleGlobalException(ex, webRequest);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), response.getBody().getStatus());
        assertEquals("An unexpected error occurred", response.getBody().getMessage());
    }
} 