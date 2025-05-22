package com.orderme.backend.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        userService = new UserService(userRepository, passwordEncoder);
    }

    @Test
    void getAllUsers_ShouldReturnAllUsers() {
        // Arrange
        List<User> expectedUsers = createSampleUsers();
        when(userRepository.findAll()).thenReturn(expectedUsers);

        // Act
        List<User> actualUsers = userService.getAllUsers();

        // Assert
        assertEquals(expectedUsers, actualUsers);
        verify(userRepository).findAll();
    }

    @Test
    void getUserById_ShouldReturnUser() {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Act
        Optional<User> foundUser = userService.getUserById(1L);

        // Assert
        assertTrue(foundUser.isPresent());
        assertEquals(1L, foundUser.get().getId());
        assertEquals("testuser", foundUser.get().getUsername());
    }

    @Test
    void getUserById_ShouldReturnEmpty_WhenUserNotFound() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        Optional<User> foundUser = userService.getUserById(1L);

        // Assert
        assertTrue(foundUser.isEmpty());
    }

    @Test
    void getUserByUsername_ShouldReturnUser() {
        // Arrange
        User expectedUser = createSampleUsers().get(0);
        when(userRepository.findByUsername("john.doe")).thenReturn(Optional.of(expectedUser));

        // Act
        Optional<User> actualUser = userService.getUserByUsername("john.doe");

        // Assert
        assertTrue(actualUser.isPresent());
        assertEquals(expectedUser, actualUser.get());
        verify(userRepository).findByUsername("john.doe");
    }

    @Test
    void createUser_ShouldEncodePasswordAndSave() {
        // Arrange
        User user = new User();
        user.setUsername("testuser");
        user.setPassword("password");
        user.setRole("USER");

        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        User savedUser = userService.createUser(user);

        // Assert
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User capturedUser = userCaptor.getValue();

        assertEquals("testuser", capturedUser.getUsername());
        assertEquals("encodedPassword", capturedUser.getPassword());
        assertEquals("USER", capturedUser.getRole());
    }

    @Test
    void updateUser_ShouldUpdateUser() {
        // Arrange
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("olduser");
        existingUser.setPassword("oldpassword");
        existingUser.setRole("USER");

        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setUsername("newuser");
        updatedUser.setPassword("newpassword");
        updatedUser.setRole("ADMIN");

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));
        when(passwordEncoder.encode("newpassword")).thenReturn("encodedNewPassword");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        // Act
        User result = userService.updateUser(1L, updatedUser);

        // Assert
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User capturedUser = userCaptor.getValue();

        assertEquals("newuser", capturedUser.getUsername());
        assertEquals("encodedNewPassword", capturedUser.getPassword());
        assertEquals("ADMIN", capturedUser.getRole());
    }

    @Test
    void updateUser_ShouldReturnNull_WhenUserNotFound() {
        // Arrange
        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setUsername("newuser");

        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> userService.updateUser(1L, updatedUser));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void deleteUser_ShouldDeleteUser() {
        // Arrange
        User user = new User();
        user.setId(1L);
        user.setUsername("testuser");

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Act
        userService.deleteUser(1L);

        // Assert
        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteUser_ShouldNotDelete_WhenUserNotFound() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act
        userService.deleteUser(1L);

        // Assert
        verify(userRepository).deleteById(1L);
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