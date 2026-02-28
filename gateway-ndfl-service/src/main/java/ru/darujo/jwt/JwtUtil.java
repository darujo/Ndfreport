package ru.darujo.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.Date;

@Component
public class JwtUtil {
    @Value("${jwt.secretKey}")
    private String secretKey;

    public Claims getAllClamsForToken(String token){
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();
    }
    private boolean isTokenExpired (String token){
        try {
            return this.getAllClamsForToken(token).getExpiration().before(new Date());
        }
        catch (ExpiredJwtException ex){
            return true;
        }

    }
    public boolean isInvalid(String token){
        return this.isTokenExpired(token);
    }
}
