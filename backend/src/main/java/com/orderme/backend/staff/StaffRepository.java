package com.orderme.backend.staff;

import com.orderme.backend.staff.Staff;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface StaffRepository extends JpaRepository<Staff, Long> {
    Optional<Staff> findByUsernameAndPassword(String username, String password);
    Optional<Staff> findByUsername(String username);
}
