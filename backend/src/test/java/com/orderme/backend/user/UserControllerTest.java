package com.orderme.backend.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {

    @Mock
    private UserService userService;

    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userController = new UserController(userService);
    }

    @Test
    void getAllUsers_ShouldReturnAllUsers() {
        // Arrange
        List<User> expectedUsers = createSampleUsers();
        when(userService.getAllUsers()).thenReturn(expectedUsers);

        // Act
        List<User> actualUsers = userController.getAllUsers();

        // Assert
        assertEquals(expectedUsers, actualUsers);
        verify(userService).getAllUsers();
    }

    @Test
    void getUserById_ShouldReturnUser_WhenUserExists() {
        // Arrange
        User expectedUser = createSampleUsers().get(0);
        when(userService.getUserById(1L)).thenReturn(Optional.of(expectedUser));

        // Act
        ResponseEntity<User> response = userController.getUserById(1L);

        // Assert
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertEquals(expectedUser, response.getBody());
        verify(userService).getUserById(1L);
    }

    @Test
    void getUserById_ShouldReturnNotFound_WhenUserDoesNotExist() {
        // Arrange
        when(userService.getUserById(1L)).thenReturn(Optional.empty());

        // Act
        ResponseEntity<User> response = userController.getUserById(1L);

        // Assert
        assertTrue(response.getStatusCode().is4xxClientError());
        verify(userService).getUserById(1L);
    }

    @Test
    void createUser_ShouldCreateAndReturnUser() {
        // Arrange
        User user = createSampleUsers().get(0);
        when(userService.createUser(user)).thenReturn(user);

        // Act
        User createdUser = userController.createUser(user);

        // Assert
        assertEquals(user, createdUser);
        verify(userService).createUser(user);
    }

    @Test
    void updateUser_ShouldUpdateAndReturnUser_WhenUserExists() {
        // Arrange
        User user = createSampleUsers().get(0);
        when(userService.updateUser(1L, user)).thenReturn(user);

        // Act
        ResponseEntity<User> response = userController.updateUser(1L, user);

        // Assert
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertEquals(user, response.getBody());
        verify(userService).updateUser(1L, user);
    }

    @Test
    void updateUser_ShouldReturnNotFound_WhenUserDoesNotExist() {
        // Arrange
        User user = createSampleUsers().get(0);
        when(userService.updateUser(1L, user)).thenThrow(new RuntimeException("User not found"));

        // Act
        ResponseEntity<User> response = userController.updateUser(1L, user);

        // Assert
        assertTrue(response.getStatusCode().is4xxClientError());
        verify(userService).updateUser(1L, user);
    }

    @Test
    void deleteUser_ShouldDeleteUser() {
        // Act
        ResponseEntity<Void> response = userController.deleteUser(1L);

        // Assert
        assertTrue(response.getStatusCode().is2xxSuccessful());
        verify(userService).deleteUser(1L);
    }

    private List<User> createSampleUsers() {
        User user1 = new User();
        user1.setId(1L);
        user1.setUsername("john.doe");
        user1.setPassword("password123");
        user1.setRole("ROLE_USER");

        User user2 = new User();
        user2.setId(2L);
        user2.setUsername("jane.doe");
        user2.setPassword("password456");
        user2.setRole("ROLE_ADMIN");

        return Arrays.asList(user1, user2);
    }
} 