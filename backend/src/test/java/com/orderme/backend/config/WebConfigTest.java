package com.orderme.backend.config;

import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.CorsRegistration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import static org.mockito.Mockito.*;

class WebConfigTest {

    @Test
    void addCorsMappings_ShouldConfigureCorsCorrectly() {
        // Arrange
        WebConfig webConfig = new WebConfig();
        CorsRegistry registry = mock(CorsRegistry.class);
        CorsRegistration registration = mock(CorsRegistration.class);
        when(registry.addMapping(any())).thenReturn(registration);
        doReturn(registration).when(registration).allowedOrigins(anyString());
        doReturn(registration).when(registration).allowedMethods(any(String[].class));
        doReturn(registration).when(registration).allowedHeaders(anyString());
        doReturn(registration).when(registration).exposedHeaders(anyString());
        doReturn(registration).when(registration).allowCredentials(anyBoolean());

        // Act
        webConfig.addCorsMappings(registry);

        // Assert
        verify(registry).addMapping("/**");
        verify(registration).allowedOrigins("http://localhost:5500");
        verify(registration).allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH");
        verify(registration).allowedHeaders("*");
        verify(registration).exposedHeaders("Authorization");
        verify(registration).allowCredentials(true);
    }
} 