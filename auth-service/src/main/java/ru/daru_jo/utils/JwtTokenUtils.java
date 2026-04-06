package ru.daru_jo.utils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.daru_jo.model.User;

import javax.crypto.SecretKey;
import java.util.*;


@Component
public class JwtTokenUtils {
    @Value("${jwt.secretKey}")
    private String secretKey;
    @Value("${jwt.lifeTimeToken}")
    private Integer lifeTimeToken;

    public String generateToken(User userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", userDetails.getNikName());
        Date issuedDate = new Date();
        Date expiredDate = new Date(issuedDate.getTime() + lifeTimeToken);
        return Jwts.builder()
                .subject(userDetails.getNikName())
                .issuedAt(issuedDate)
                .expiration(expiredDate)
                .claims(claims)
                .signWith(key(), Jwts.SIG.HS256)
                .compact();
    }

    private SecretKey key() {
        return Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(secretKey)
        );
    }
}
