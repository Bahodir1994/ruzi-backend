package app.ruzi.configuration.annotation.auth;

import app.ruzi.configuration.jwt.JwtUtils;
import app.ruzi.configuration.jwt.RoleAndPermissionDto;
import app.ruzi.configuration.jwt.UserJwt;
import app.ruzi.configuration.utils.CurrentUserProvider;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Aspect
@Component
@RequiredArgsConstructor
public class CustomAuthAspect {
    private final JwtUtils jwtUtils;

    @Pointcut("@annotation(app.ruzi.configuration.annotation.auth.CustomAuthRole)")
    public void customAuthMethod() {
    }

    @Before("customAuthMethod() && @annotation(customAuthRole)")
    public void checkUserRole(CustomAuthRole customAuthRole) throws Exception {
        UserJwt userJwt = jwtUtils.extractUserFromToken();

        CurrentUserProvider.setCurrentUser(userJwt.getUsername());

        String[] requiredRoles = customAuthRole.roles();
        String[] requiredPermissions = customAuthRole.permissions();

        List<RoleAndPermissionDto> needAccessList = new ArrayList<>();
        for (int i = 0; i < requiredRoles.length; i++) {
            RoleAndPermissionDto needAccessOne = new RoleAndPermissionDto();
            needAccessOne.setRoleName(requiredRoles[i]);
            List<String> permissions = new ArrayList<>();
            for (int j = 0; j < requiredPermissions.length; j++) {
                if (requiredPermissions[j].startsWith(requiredRoles[i])) {
                    permissions.add(requiredPermissions[j].toString().replace(requiredRoles[i].toString() + "_", ""));
                }
            }
            needAccessOne.setPermissions(permissions);
            needAccessList.add(needAccessOne);
        }
        if (requiredRoles.length > 0) {
            if (!userHasRequiredRoles(userJwt.getRoles(), requiredRoles)) {
                throw new RuntimeException("Forbidden_annotation");
            }
            if (requiredPermissions.length > 0 && !userHasRequiredPermissionsForRoles(userJwt.getRoles(), requiredRoles, requiredPermissions)) {
                throw new RuntimeException("Forbidden_annotation");
            }
        } else {
            if (requiredPermissions.length > 0 && !userHasRequiredPermissions(userJwt.getRoles(), requiredPermissions)) {
                throw new RuntimeException("Forbidden_annotation");
            }
        }
    }

    private boolean userHasRequiredPermissions(List<RoleAndPermissionDto> userRoles, String[] requiredPermissions) {
        return userRoles.stream()
                .flatMap(userRole -> userRole.getPermissions().stream())
                .anyMatch(permission -> Arrays.asList(requiredPermissions).contains(permission));
    }

    private boolean userHasRequiredPermissionsForRoles(List<RoleAndPermissionDto> userRoles, String[] requiredRoles, String[] requiredPermissions) {
        return userRoles.stream()
                .filter(userRole -> Arrays.asList(requiredRoles).contains(userRole.getRoleName()))
                .flatMap(userRole -> userRole.getPermissions().stream())
                .anyMatch(permission -> Arrays.asList(requiredPermissions).contains(permission));
    }

    private boolean userHasRequiredRoles(List<RoleAndPermissionDto> userRoles, String[] requiredRoles) {
        return Arrays.stream(requiredRoles)
                .anyMatch(requiredRole -> userRoles.stream()
                        .anyMatch(userRole -> userRole.getRoleName().equals(requiredRole))
                );
    }
}
