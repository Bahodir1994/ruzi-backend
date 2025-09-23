//package app.ruzi.security;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.server.reactive.ServerHttpRequest;
//import org.springframework.security.authorization.AuthorizationDecision;
//import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
//import org.springframework.security.config.web.server.ServerHttpSecurity;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.oauth2.jwt.NimbusReactiveJwtDecoder;
//import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
//import org.springframework.security.web.server.SecurityWebFilterChain;
//import org.springframework.security.web.server.authorization.AuthorizationContext;
//import org.springframework.web.cors.CorsConfiguration;
//import org.springframework.web.cors.reactive.CorsConfigurationSource;
//import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
//import reactor.core.publisher.Mono;
//
//import java.util.Arrays;
//import java.util.List;
//
//@Configuration
//@EnableWebFluxSecurity
//public class WebSecurityConfig {
//
//    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
//    private String jwkSetUri;
//
//    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
//    private String jwtUri;
//
//    @Bean
//    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
//        http
//                .cors((cors) -> cors
//                        .configurationSource(corsConfigurationSource())
//                )
//                .csrf(ServerHttpSecurity.CsrfSpec::disable)
//                .authorizeExchange(authorizeExchangeSpec -> authorizeExchangeSpec
//                        .pathMatchers("/route-catalog/sql-query/bot").permitAll()
//                        .pathMatchers("/sptoauth/isigner/**").permitAll()
//                        .pathMatchers("/sptoauth/skvnksjdhfjksd.do/**")
//                        .access(this::checkRequestMethodAndIp)
//                        .anyExchange().authenticated()
//                )
//                .oauth2ResourceServer(
//                        oauth2 -> oauth2
//                                .jwt(jwt -> jwt.jwtDecoder(reactiveJwtDecoder()))
//                );
//        return http.build();
//    }
//
//    private Mono<AuthorizationDecision> checkRequestMethodAndIp(Mono<Authentication> authenticationMono, AuthorizationContext authorizationContext) {
//        ServerHttpRequest request = authorizationContext.getExchange().getRequest();
//
//        if (request.getMethod().equals(HttpMethod.POST) &&
//                (
//                        "http://172.16.112.7:9080/".equals(request.getHeaders().getFirst("Referer"))
//                        || "http://172.16.112.20:9080/".equals(request.getHeaders().getFirst("Referer"))
//                        || "http://192.168.224.18:4200".equals(request.getHeaders().getFirst("Referer"))
//                        || "http://172.16.212.11:4200".equals(request.getHeaders().getFirst("Referer"))
//                )
//        ) {
//            return Mono.just(new AuthorizationDecision(true));
//        }
//        return Mono.just(new AuthorizationDecision(false));
//    }
//
//    private ReactiveJwtDecoder reactiveJwtDecoder() {
//        return NimbusReactiveJwtDecoder.withJwkSetUri(jwkSetUri).build();
//    }
//
//    /*
//    @Bean
//    @Order(1)
//    public SecurityWebFilterChain myOtherFilterChain(ServerHttpSecurity http) {
//        http
//                .csrf(ServerHttpSecurity.CsrfSpec::disable)
//                .cors((cors) -> cors
//                        .configurationSource(esadCorsConfigurationSource()))
//                .authorizeExchange(
//                        authorizeExchangeSpec -> authorizeExchangeSpec
//                                .pathMatchers("/sptoauth/skvnksjdhfjksd.do/**").permitAll()
//                        .anyExchange().authenticated())
//        ;
//        return http.build();
//    }
//     */
//
//    CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration configuration = new CorsConfiguration();
//        configuration.setAllowedOrigins(List.of(
//                "http://172.16.212.11:4200",
//                "http://192.168.224.18:4200",
//                "http://172.16.112.7:9080",
//                "http://172.16.112.20:9080",
//                "http://192.168.224.13:4200",
//                "http://10.190.0.118:4300",
//                "http://10.190.0.118:4500",
//                "http://10.190.0.118:8765"
//        ));
//        configuration.setAllowedMethods(Arrays.asList("GET","POST", "PATCH", "PUT", "DELETE"));
//        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
//        configuration.setAllowCredentials(true);
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", configuration);
//        return source;
//    }
//
//    /*
//    CorsConfigurationSource esadCorsConfigurationSource() {
//        CorsConfiguration configuration = new CorsConfiguration();
//        configuration.setAllowedOrigins(Arrays.asList("http://172.16.112.7:9080", "http://172.16.112.20:9080"));
//        configuration.setAllowedMethods(List.of("POST"));
//        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
//        configuration.setAllowCredentials(true);
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/sptoauth/skvnksjdhfjksd.do/**", configuration);
//        return source;
//    }
//     */
//}
//    /*
//    @Bean
//    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
//        http
//            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
//            .csrf(csrf -> csrf
//                .requireCsrfProtectionMatcher(new AntPathRequestMatcher("/sptoauth/skvnksjdhfjksd.do/**", HttpMethod.POST.name())) // Включаем CSRF для POST-запросов на /sptoauth/skvnksjdhfjksd.do/**
//                .disable() // Отключаем CSRF для всех запросов, подходящих под AntPathRequestMatcher
//            )
//            .authorizeExchange(authorizeExchangeSpec -> authorizeExchangeSpec
//                .pathMatchers("/sptoauth/skvnksjdhfjksd.do/**")
//                    .access(this::checkRequestMethodAndIp) // Проверка метода и IP-адреса
//                .anyExchange().authenticated()
//            );
//
//        return http.build();
//    }
//
//    private Mono<AuthorizationDecision> checkRequestMethodAndIp(AuthorizationContext context) {
//        ServerHttpRequest request = context.getExchange().getRequest();
//
//        // Проверяем, что метод POST и IP-адрес один из указанных
//        if (request.getMethod().equals(HttpMethod.POST) &&
//            (
//                "172.16.112.7".equals(request.getRemoteAddress().getAddress().getHostAddress()) ||
//                "172.16.112.20".equals(request.getRemoteAddress().getAddress().getHostAddress())
//            )) {
//            return Mono.just(new AuthorizationDecision(true)); // Разрешаем доступ
//        }
//
//        return Mono.just(new AuthorizationDecision(false)); // Отказываем в доступе
//    }
//     */