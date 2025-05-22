package com.orderme.backend.staff;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.time.LocalDateTime;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class StaffServiceTest {

    @Mock
    private StaffRepository staffRepository;

    private StaffService staffService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        staffService = new StaffService(staffRepository);
    }

    @Test
    void findByUsername_ShouldReturnStaff() {
        // Arrange
        Staff expectedStaff = createSampleStaff();
        when(staffRepository.findByUsername("john.doe")).thenReturn(Optional.of(expectedStaff));

        // Act
        Optional<Staff> actualStaff = staffService.findByUsername("john.doe");

        // Assert
        assertTrue(actualStaff.isPresent());
        assertEquals(expectedStaff, actualStaff.get());
        verify(staffRepository).findByUsername("john.doe");
    }

    @Test
    void findByUsername_ShouldReturnEmptyForNonExistentUser() {
        // Arrange
        when(staffRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

        // Act
        Optional<Staff> actualStaff = staffService.findByUsername("nonexistent");

        // Assert
        assertTrue(actualStaff.isEmpty());
        verify(staffRepository).findByUsername("nonexistent");
    }

    private Staff createSampleStaff() {
        Staff staff = new Staff("john.doe", "password123", "waiter");
        staff.setStaffid(1L);
        staff.setCreatedat(LocalDateTime.now());
        return staff;
    }
} 