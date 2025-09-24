package app.ruzi.configuration.jwt;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.io.IOException;

public class JwtTokenFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String token = httpRequest.getHeader("Authorization");


        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            RequestContextHolder.getRequestAttributes().setAttribute("token", token, RequestAttributes.SCOPE_REQUEST);
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }

}
