package app.ruzi.configuration.jwt;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserJwt {
    private String userId;
    private String username;
    private String fullName;
    private String locationCode;
    private String postId;
    private List<RoleAndPermissionDto> roles;
}
