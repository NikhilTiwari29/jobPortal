package com.jobPortal.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtService {

    // 🔑 Secret key (must be at least 256 bits for HS256)
    private static final String SECRET_KEY = "mysupersecretkeymysupersecretkeymysupersecretkey";

    // ✅ Generate token for a given username
    public String generateToken(String userId) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userId);
    }

    // ✅ Decode secret key
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // ✅ Create the JWT token
    private String createToken(Map<String, Object> claims, String userId) {

        return Jwts.builder()

                // ===== HEADER =====
                // (alg + typ automatically added by .signWith())

                // ===== PAYLOAD =====
                .claims(claims)  //<----- custom claims (mostly used to add roles)
                .subject(userId)  //<------------------   (unique identifier to know this token belongs to which user)
                .issuedAt(new Date(System.currentTimeMillis())) // when the token is issued
                .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // when token will expired

                // ===== SIGNATURE =====
                .signWith(getSignInKey())             // sets alg=HS256, signs with key

                // Final JWT string: base64Url(header).base64Url(payload).signature
                .compact();


    }

    // ✅ Validate token
    public boolean isTokenValid(String token, UserDetails userDetails) {
        Long extractedUserId = extractUserId(token);
        return (extractedUserId.equals(Long.parseLong(userDetails.getUsername()))
                && !isTokenExpired(token));
    }


    // ✅ Extract username (subject) from token
    public Long extractUserId(String token) {
        return Long.parseLong(extractClaim(token, Claims::getSubject));
    }

    // ✅ Generic claim extractor
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // ✅ Check if expired
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // ✅ Parse all claims
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith((SecretKey) getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
