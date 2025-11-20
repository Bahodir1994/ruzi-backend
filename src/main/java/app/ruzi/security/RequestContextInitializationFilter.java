package app.ruzi.security;

import app.ruzi.configuration.jwt.JwtUtils;
import app.ruzi.configuration.jwt.UserJwt;
import app.ruzi.configuration.utils.CurrentTenantProvider;
import app.ruzi.configuration.utils.CurrentUserProvider;
import app.ruzi.configuration.utils.CurrentWarehouseProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class RequestContextInitializationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        try {
            UserJwt userJwt = jwtUtils.extractUserFromToken();

            // 🔹 ThreadLocal-larni to‘ldiramiz
            CurrentUserProvider.setCurrentUser(userJwt.getUsername());
            CurrentTenantProvider.setCurrentClient(userJwt.getClientId());
            CurrentWarehouseProvider.setCurrentWarehouse(userJwt.getWarehouseId());

        } catch (RuntimeException e) {
            // Token yo‘q yoki noto‘g‘ri bo‘lsa — bu filter xatoga to‘xtamaydi,
            // Spring Security o‘zi tokenni tekshiradi
        }

        filterChain.doFilter(request, response);
    }
}

