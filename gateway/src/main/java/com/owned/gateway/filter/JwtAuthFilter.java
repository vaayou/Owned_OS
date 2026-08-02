package com.owned.gateway.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.*;

/**
 * JWT validation servlet filter for Spring Cloud Gateway (MVC / servlet stack).
 *
 * <p>Validates the Bearer token once at the gateway. On success, injects
 * {@code X-User-Id} and {@code X-Username} headers into the proxied request
 * so downstream services can trust them without needing JWT libraries.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final List<String> OPEN_PATHS = List.of(
            "/api/auth/login",
            "/api/auth/register",
            "/api/auth/refresh",
            "/api/payments/webhook",
            "/api/products",
            "/api/categories",
            "/actuator"
    );

    private final SecretKey signingKey;

    public JwtAuthFilter(SecretKey signingKey) {
        this.signingKey = signingKey;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        // Allow CORS pre-flight OPTIONS requests through immediately
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }

        String path = request.getRequestURI();

        // Let public endpoints through immediately
        if (OPEN_PATHS.stream().anyMatch(path::startsWith)) {
            filterChain.doFilter(request, response);
            return;
        }

        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return;
        }

        try {
            Claims claims = Jwts.parser()
                    .verifyWith(signingKey)
                    .build()
                    .parseSignedClaims(authHeader.substring(7))
                    .getPayload();

            String userId   = claims.getSubject();
            String username = claims.get("username", String.class);

            // Wrap the request so downstream sees the extra trusted headers
            HttpServletRequest mutated = new HeaderInjectingRequestWrapper(request, userId, username);
            filterChain.doFilter(mutated, response);

        } catch (Exception e) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
        }
    }

    // -------------------------------------------------------------------------
    // Inner wrapper — adds X-User-Id / X-Username to the immutable header map
    // -------------------------------------------------------------------------

    private static final class HeaderInjectingRequestWrapper extends HttpServletRequestWrapper {

        private final Map<String, String> extraHeaders;

        HeaderInjectingRequestWrapper(HttpServletRequest request, String userId, String username) {
            super(request);
            this.extraHeaders = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
            if (userId   != null) extraHeaders.put("X-User-Id",  userId);
            if (username != null) extraHeaders.put("X-Username", username);
        }

        @Override
        public String getHeader(String name) {
            String extra = extraHeaders.get(name);
            return extra != null ? extra : super.getHeader(name);
        }

        @Override
        public Enumeration<String> getHeaders(String name) {
            String extra = extraHeaders.get(name);
            if (extra != null) {
                return Collections.enumeration(List.of(extra));
            }
            return super.getHeaders(name);
        }

        @Override
        public Enumeration<String> getHeaderNames() {
            Set<String> names = new LinkedHashSet<>(extraHeaders.keySet());
            Enumeration<String> original = super.getHeaderNames();
            while (original.hasMoreElements()) {
                names.add(original.nextElement());
            }
            return Collections.enumeration(names);
        }
    }
}
