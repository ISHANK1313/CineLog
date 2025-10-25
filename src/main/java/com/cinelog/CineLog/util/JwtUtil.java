package com.cinelog.CineLog.util;


import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtil {
    private static final String SECRET="vbidvbosbsvoessbouqeuqegrpqgtibg31gto230ybtb2-gbvvg25u2y-569yt29y28v10t";
    private Key getSigningKey(){
        return Keys.hmacShaKeyFor(SECRET.getBytes());
    }
    public String createToken(Map<String,Object> mp, String email){
        return Jwts.builder()
                .setClaims(mp).signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .setSubject(email)
                .setExpiration(new Date(System.currentTimeMillis()+86400000))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .compact();
    }

    public boolean isTokenValid(String token){
        if (token == null || token.trim().isEmpty()) {
            return false;
        }
       try{

                Jwts.parserBuilder().setSigningKey(getSigningKey()).build()
                        .parseClaimsJws(token).getBody();
              return true;



         } catch (Exception e) {
           return false;
       }

    }
    public String getEmailFromToken(String token){
        return Jwts.parserBuilder().setSigningKey(getSigningKey())
                .build().parseClaimsJws(token)
                .getBody().getSubject();
    }
}
