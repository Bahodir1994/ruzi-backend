package app.ruzi.configuration.jwt;

import com.nimbusds.jwt.JWT;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.JWTParser;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class JwtUtils {

    public UserJwt extractUserFromToken() throws ParseException {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String token = request.getHeader("Authorization");

        if (token == null || token.isEmpty()) {
            throw new RuntimeException("Forbidden_annotation");
        }

        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        JWT jwt = JWTParser.parse(token);

        JWTClaimsSet claims = jwt.getJWTClaimsSet();
        String username = claims.getStringClaim("preferred_username");
        String userId = claims.getStringClaim("sub");
        String clientId = "client1";
        String warehouseId = "WH-001";
        String fullName = claims.getStringClaim("name");
        String locationCode = claims.getStringClaim("location_code");
        String postId = claims.getStringClaim("post_code");

        Map<String, Object> bkoAccess = (Map<String, Object>) claims.getClaim("resource_access");
        if (bkoAccess == null || !bkoAccess.containsKey("ruzi")) {
            throw new RuntimeException("Forbidden");
        }
        Map<String, Object> bkoRolesObject = (Map<String, Object>) bkoAccess.get("ruzi");
        if (bkoRolesObject == null || !bkoRolesObject.containsKey("roles")) {
            throw new RuntimeException("Forbidden");
        }
        List<String> roles = (List<String>) bkoRolesObject.get("roles");

        Map<String, List<String>> roleMap = roles.stream()
                .filter(role -> role.startsWith("ROLE_") && role.contains("_PERMISSION_"))
                .collect(Collectors.groupingBy(
                        role -> {
                            int start = role.indexOf("ROLE_") + "ROLE_".length();
                            int end = role.indexOf("_PERMISSION_");
                            return role.substring(start, end);
                        },
                        Collectors.mapping(
                                role -> {
                                    int start = role.indexOf("_PERMISSION_") + "_PERMISSION_".length();
                                    return role.substring(start);
                                },
                                Collectors.toList()
                        )
                ));

        List<RoleAndPermissionDto> roleAndPermissionDtos = roleMap.entrySet().stream()
                .map(entry -> new RoleAndPermissionDto(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());


        List<RoleAndPermissionDto> roleAndPermission = roles.stream()
                .map(entry -> new RoleAndPermissionDto(entry, null))
                .collect(Collectors.toList());

        return new UserJwt(
                userId,
                clientId,
                warehouseId,
                username,
                fullName,
                locationCode,
                postId,
                roleAndPermission
        );
    }

}
