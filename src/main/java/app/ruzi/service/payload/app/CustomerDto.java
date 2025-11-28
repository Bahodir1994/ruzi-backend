package app.ruzi.service.payload.app;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDate;

/**
 * DTO for {@link app.ruzi.entity.app.Customer}
 */
@AllArgsConstructor
@Getter
public class CustomerDto implements Serializable {
    private final String insUser;
    private final String updUser;
    private final Timestamp insTime;
    private final Timestamp updTime;
    private final Boolean isDeleted;
    private final String id;
    private final String clientCode;
    private final String fullName;
    private final String phoneNumber;
    private final String email;
    private final String gender;
    private final LocalDate birthDate;
    private final String customerType;
    private final String companyName;
    private final String tin;
    private final String region;
    private final String address;
    private final String note;
    private final Double loyaltyPoints;
    private final Boolean isActive;
}