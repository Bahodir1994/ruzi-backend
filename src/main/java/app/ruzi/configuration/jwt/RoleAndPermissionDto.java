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
public class RoleAndPermissionDto {
    private String roleName;
    private List<String> permissions;

    public RoleAndPermissionDto(List<String> permissions) {
        this.permissions = permissions;
    }
}
