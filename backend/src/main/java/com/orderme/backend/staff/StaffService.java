package com.orderme.backend.staff;

import com.orderme.backend.staff.Staff;
import com.orderme.backend.staff.StaffRepository;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class StaffService {

    private final StaffRepository staffRepository;

    public StaffService(StaffRepository staffRepository) {
        this.staffRepository = staffRepository;
    }

    public Staff authenticate(String username, String password) {
        return staffRepository.findByUsernameAndPassword(username, password).orElse(null);
    }

    public Optional<Staff> findByUsername(String username) {
        return staffRepository.findByUsername(username);
    }
}
