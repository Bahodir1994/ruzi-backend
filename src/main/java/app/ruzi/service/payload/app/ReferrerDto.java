package app.ruzi.service.payload.app;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * DTO for {@link app.ruzi.entity.app.Referrer}
 */
@AllArgsConstructor
@Getter
public class ReferrerDto implements Serializable {
    private final String insUser;
    private final String updUser;
    private final Timestamp insTime;
    private final Timestamp updTime;
    private final Boolean isDeleted;
    private final String id;
    private final String referrerCode;
    private final String fullName;
    private final String phone;
    private final BigDecimal balance;
}