package com.orderme.backend.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ErrorResponseTest {

    @Test
    void testErrorResponseCreation() {
        // Arrange
        int status = 404;
        String message = "Not Found";
        String details = "Resource not found";

        // Act
        ErrorResponse errorResponse = new ErrorResponse(status, message, details);

        // Assert
        assertEquals(status, errorResponse.getStatus());
        assertEquals(message, errorResponse.getMessage());
        assertEquals(details, errorResponse.getDetails());
    }

    @Test
    void testErrorResponseSetters() {
        // Arrange
        ErrorResponse errorResponse = new ErrorResponse(500, "Error", "Details");

        // Act
        errorResponse.setStatus(404);
        errorResponse.setMessage("Not Found");
        errorResponse.setDetails("Resource not found");

        // Assert
        assertEquals(404, errorResponse.getStatus());
        assertEquals("Not Found", errorResponse.getMessage());
        assertEquals("Resource not found", errorResponse.getDetails());
    }
} 