package com.owned.user_service.security;

import com.owned.user_service.entity.User;
import com.owned.user_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

/**
 * Bridges Spring Security's UserDetailsService with our User entity.
 * Used only by the {@link org.springframework.security.authentication.AuthenticationManager}
 * during credential validation — NOT for per-request authentication (that is
 * handled via the X-User-Id header injected by the Gateway).
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found: " + username));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPasswordHash())
                .roles("USER")
                .build();
    }
}
