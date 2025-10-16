package app.ruzi.configuration.utils;

import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.List;

@Component
@RequiredArgsConstructor
public class HibernateTenantFilterInterceptor implements HandlerInterceptor {

    private final EntityManager entityManager;

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) {

        // 🔹 Hozirgi foydalanuvchining client_id sini olish
        String clientId = CurrentTenantProvider.getCurrentClient();
        List<String> currentRoles = CurrentUserProvider.getCurrentRoles();

        // 🔹 Agar u super_admin bo‘lmasa — filterni yoqamiz
        Session session = entityManager.unwrap(Session.class);
        if (clientId != null && !currentRoles.contains("super_admin")) {
            session.enableFilter("clientFilter")
                    .setParameter("clientId", clientId);
        } else {
            session.disableFilter("clientFilter");
        }

        return true; // so‘rovni davom ettiradi
    }
}

