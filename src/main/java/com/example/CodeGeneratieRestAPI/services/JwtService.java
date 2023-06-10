package com.example.CodeGeneratieRestAPI.services;

import com.example.CodeGeneratieRestAPI.jwt.JwtKeyProvider;
import com.example.CodeGeneratieRestAPI.models.UserType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtService {

    @Autowired
    JwtKeyProvider keyProvider;

    // Returns the JWT token from the Authorization header or null if the token is invalid
    public String getJwtToken(String bearerToken) {
        if (CheckTokenValidity(bearerToken)) {
            return bearerToken.substring(7);
        }
        return null;
    }

    // Returns the userId from the JWT token or -1 if the token is invalid
    public long getUserIdFromJwtToken(String bearerToken) {
        String token = getJwtToken(bearerToken);
        if (token != null) {
            return Jwts.parserBuilder().setSigningKey(keyProvider.getPrivateKey()).build().parseClaimsJws(token)
                    .getBody().get("userId", Long.class);
        }
        return -1;
    }

    // Returns the username from the JWT token or null if the token is invalid
    public String getUsernameFromJwtToken(String bearerToken) {
        String token = getJwtToken(bearerToken);
        if (token != null) {
            return Jwts.parserBuilder().setSigningKey(keyProvider.getPrivateKey()).build().parseClaimsJws(token)
                    .getBody().getSubject();
        }
        return null;
    }

    // Returns the user type from the JWT token or null if the token is invalid
    public Enum<UserType> getUserTypeFromJwtToken(String bearerToken) {
        String token = getJwtToken(bearerToken);
        if (token != null) {
            return Jwts.parserBuilder().setSigningKey(keyProvider.getPrivateKey()).build().parseClaimsJws(token)
                    .getBody().get("userType", UserType.class);
        }
        return null;
    }

    // Returns true if the JWT token is valid
    public boolean validateJwtToken(String authbearerToken) {
        String token = getJwtToken(authbearerToken);
        if (token != null) {
            try {
                Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(keyProvider.getPrivateKey()).build()
                        .parseClaimsJws(token);
                return !claims.getBody().getExpiration().before(new Date());
            } catch (JwtException | IllegalArgumentException e) {
                throw new JwtException("Expired or invalid JWT token");
            }
        }
        return false;
    }

    // Returns true if the Authorization header is valid
    private boolean CheckTokenValidity(String bearerToken) {
        return bearerToken != null && bearerToken.startsWith("Bearer ");
    }
}
