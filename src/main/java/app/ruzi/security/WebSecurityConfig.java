package app.ruzi.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class WebSecurityConfig {

    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    private String jwkSetUri;

    /**
     * 🔐 Asosiy xavfsizlik konfiguratsiyasi
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 🔹 CORS sozlamalari
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                // 🔹 CSRF o‘chirildi (faqat API uchun)
                .csrf(csrf -> csrf.disable())
                // 🔹 So‘rovlarni ruxsatlash qoidalari
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/route-catalog/sql-query/bot").permitAll()
                        .requestMatchers("/sptoauth/isigner/**").permitAll()
                        .requestMatchers("/sptoauth/skvnksjdhfjksd.do/**")
                        .access((authentication, context) ->
                                checkRequestMethodAndIp(context.getRequest()))
                        .anyRequest().authenticated()
                )
                // 🔹 JWT resurs server
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.decoder(jwtDecoder())));

        return http.build();
    }

    /**
     * 🧩 JWT decoder (Keycloak yoki boshqa IdP)
     */
    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();
    }

    /**
     * 🧩 POST so‘rovlarda referer/IP tekshiruvi
     */
    private org.springframework.security.authorization.AuthorizationDecision checkRequestMethodAndIp(HttpServletRequest request) {
        if (HttpMethod.POST.matches(request.getMethod())) {
            String referer = request.getHeader("Referer");
            if (
                    "http://172.16.112.7:9080/".equals(referer) ||
                            "http://172.16.112.20:9080/".equals(referer) ||
                            "http://192.168.224.18:4200".equals(referer) ||
                            "http://172.16.212.11:4200".equals(referer)
            ) {
                return new org.springframework.security.authorization.AuthorizationDecision(true);
            }
        }
        return new org.springframework.security.authorization.AuthorizationDecision(false);
    }

    /**
     * 🌐 CORS ruxsatlari
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("*")); // barcha domenlarga ruxsat
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
