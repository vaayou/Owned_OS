package com.owned.gateway.security;

import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Configuration
public class JwtUtil {

    /**
     * Derives a 256-bit HMAC key from the configured secret by hashing it
     * with SHA-256. This guarantees the key always meets JJWT's minimum
     * length requirement (RFC 7518 §3.2) regardless of the secret's length,
     * while remaining deterministic — the same secret always produces the
     * same key.
     */
    @Bean
    public SecretKey signingKey(@Value("${jwt.secret}") String secret) throws NoSuchAlgorithmException {
        byte[] digest = MessageDigest.getInstance("SHA-256")
                .digest(secret.getBytes(StandardCharsets.UTF_8));
        return Keys.hmacShaKeyFor(digest); // digest is always exactly 256 bits
    }
}
