package app.ruzi.configuration.cors;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Profile("dev")
@Component
public class CustomCorsFilter implements Filter {

    // Ruxsat berilgan originlar ro‘yxati (agar cheklamoqchi bo‘lsangiz)
    private static final List<String> ALLOWED_ORIGINS = List.of(
            "http://localhost:4200",
            "http://127.0.0.1:4200",
            "http://192.168.0.108:4200",
            "http://192.168.170.6:4200",
            "http://192.168.224.18:4200"
    );

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletResponse res = (HttpServletResponse) response;
        HttpServletRequest req = (HttpServletRequest) request;

        // Foydalanuvchi kelayotgan originni olamiz
        String origin = req.getHeader("Origin");

        // Agar ruxsat etilgan bo‘lsa, shuni response ga qo‘yamiz
        if (origin != null && (ALLOWED_ORIGINS.isEmpty() || ALLOWED_ORIGINS.contains(origin))) {
            res.setHeader("Access-Control-Allow-Origin", origin);
        }

        res.setHeader("Access-Control-Allow-Credentials", "true");
        res.setHeader("Access-Control-Allow-Methods", "GET, POST, PATCH, PUT, DELETE, OPTIONS");
        res.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization, Accept, Origin");
        res.setHeader("Access-Control-Max-Age", "3600");

        // WebSocket (Upgrade) so‘rovlarini qo‘llab-quvvatlash uchun
        if ("websocket".equalsIgnoreCase(req.getHeader("Upgrade"))) {
            chain.doFilter(request, response);
            return;
        }

        if ("OPTIONS".equalsIgnoreCase(req.getMethod())) {
            res.setStatus(HttpServletResponse.SC_OK);
        } else {
            chain.doFilter(request, response);
        }
    }
}
