package com.owned.user_service.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Downstream authentication filter for the User Service.
 *
 * <p>The Gateway already validated the JWT and injected two trusted headers:
 * <ul>
 *   <li>{@code X-User-Id}  — the authenticated user's database id</li>
 *   <li>{@code X-Username} — the authenticated user's username</li>
 * </ul>
 *
 * <p>This filter reads those headers and populates the {@link SecurityContextHolder}
 * so that Spring Security's method-level / endpoint-level access control works
 * correctly without re-validating the JWT on every request.
 *
 * <p><strong>Security note:</strong> Clients can only reach this service through
 * the Gateway (not directly), so these headers can be trusted. If direct access
 * is ever permitted, add IP-allowlisting or re-enable JWT validation here.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String userId   = request.getHeader("X-User-Id");
        String username = request.getHeader("X-Username");

        if (userId != null && username != null
                && SecurityContextHolder.getContext().getAuthentication() == null) {

            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(
                            username,
                            null,
                            List.of(new SimpleGrantedAuthority("ROLE_USER"))
                    );
            // Store userId in the details so controllers can retrieve it easily
            auth.setDetails(Long.parseLong(userId));
            SecurityContextHolder.getContext().setAuthentication(auth);
        }

        filterChain.doFilter(request, response);
    }
}
