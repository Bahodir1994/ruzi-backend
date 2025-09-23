package app.ruzi.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "employees", schema = "ruzi")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String fullName;

    @Column(unique = true, nullable = false, length = 50)
    private String username;

    private String passwordHash;

    private String phone;
    private String email;

    @Enumerated(EnumType.STRING)
    private Role role = Role.SELLER; // SELLER, CASHIER, ADMIN

    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE; // ACTIVE / INACTIVE

    private LocalDateTime lastLogin;

    public enum Role { SELLER, CASHIER, ADMIN }
    public enum Status { ACTIVE, INACTIVE }
}
