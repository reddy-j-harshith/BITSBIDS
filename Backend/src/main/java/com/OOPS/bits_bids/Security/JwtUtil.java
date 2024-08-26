package com.OOPS.bits_bids.Security;

import com.OOPS.bits_bids.Config.UserConfig;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
@Data
@RequiredArgsConstructor
public class JwtUtil {

    private final SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public String generateToken(UserConfig userConfig) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("bitsId", userConfig.getUsername());
        claims.put("bitsMail", userConfig.getMail());
        claims.put("authorities", userConfig.getAuthorities());
        return createToken(claims, userConfig.getUsername());
    }

    public String generateRefreshToken(UserConfig userConfig) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("bitsId", userConfig.getUsername());
        claims.put("bitsMail", userConfig.getMail());
        claims.put("authorities", userConfig.getAuthorities());
        return createRefreshToken(claims, userConfig.getUsername());
    }

    private String createRefreshToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7)) // 1 Week Validity
                .signWith(SECRET_KEY)
                .compact();
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // 10 hours
                .signWith(SECRET_KEY)
                .compact();
    }

    public Boolean validateToken(String token, UserConfig userConfig) {
        final String username = extractUsername(token);
        return (username.equals(userConfig.getUsername()) && !isTokenExpired(token));
    }

    public String extractBitsId(String token) {
        return extractClaim(token, claims -> claims.get("bitsId", String.class));
    }

    public String extractMail(String token) {
        return extractClaim(token, claims -> claims.get("mail", String.class));
    }

    public String extractPassword(String token) {
        return extractClaim(token, claims -> claims.get("password", String.class));
    }
}
