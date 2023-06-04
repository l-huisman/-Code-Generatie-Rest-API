package com.example.CodeGeneratieRestAPI.jwt;

import com.example.CodeGeneratieRestAPI.models.UserType;
import com.example.CodeGeneratieRestAPI.services.MyUserDetailService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwTokenProvider {

    @Autowired
    JwtKeyProvider keyProvider;

    @Autowired
    MyUserDetailService myUserDetailService;

    public String createToken(Long id, String username, UserType role) throws JwtException {
        Claims claims = Jwts.claims().setSubject(username);
        claims.put("userId", id);
        claims.put("role", role);
        Date now = new Date();
        Date expiration = new Date(now.getTime() + 3600000); // this second value is in milliseconds, so 3600000 is 1 hour
        return Jwts.builder().setClaims(claims).setIssuedAt(now).setExpiration(expiration)
                .signWith(keyProvider.getPrivateKey()) // <- this is important, we need a key to sign the jwt
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(keyProvider.getPrivateKey()).build()
                    .parseClaimsJws(token);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException e) {

            System.out.println(token);

            throw new JwtException("Expired or invalid JWT token");
        }
    }

    public Authentication getAuthentication(String token) {
        try {
            Jws<Claims> claims = Jwts.parserBuilder().setSigningKey(keyProvider.getPrivateKey()).build()
                    .parseClaimsJws(token);
            String username = claims.getBody().getSubject();
            UserDetails userDetails = myUserDetailService.loadUserByUsername(username);
            return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        } catch (JwtException | IllegalArgumentException e) {
            throw new JwtException("Bearer token not valid");
        }
    }

    public Long getUserIdFromJWT(String token) {
        double userID = Jwts.parserBuilder().setSigningKey(keyProvider.getPrivateKey()).build().parseClaimsJws(token)
                .getBody().get("userId", Double.class);
        return (long) userID;
    }

    public String getUserTypeFromJWT(String token) {
        String userType = Jwts.parserBuilder().setSigningKey(keyProvider.getPrivateKey()).build().parseClaimsJws(token)
                .getBody().get("role", String.class);
        return userType;
    }
}
