package com.orderme.backend.staff;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "staff")
public class Staff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long staffid;

    private String username;
    private String password;
    private String role;
    private LocalDateTime createdat;

    public Long getStaffid() {
        return staffid;
    }

    public void setStaffid(Long staffid) {
        this.staffid = staffid;
    }

    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public LocalDateTime getCreatedat() {
        return createdat;
    }

    public void setCreatedat(LocalDateTime createdat) {
        this.createdat = createdat;
    }

    public Staff() {}

    public Staff(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Staff staff = (Staff) o;
        return Objects.equals(staffid, staff.staffid) &&
               Objects.equals(username, staff.username) &&
               Objects.equals(password, staff.password) &&
               Objects.equals(role, staff.role) &&
               Objects.equals(createdat, staff.createdat);
    }

    @Override
    public int hashCode() {
        return Objects.hash(staffid, username, password, role, createdat);
    }

    // Add full constructors, getters and setters here
}
