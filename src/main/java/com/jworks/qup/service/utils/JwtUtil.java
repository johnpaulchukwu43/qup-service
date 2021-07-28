package com.jworks.qup.service.utils;

import com.jworks.app.commons.enums.Role;
import com.jworks.app.commons.exceptions.NotFoundRestApiException;
import com.jworks.qup.service.models.EndUserDto;
import com.jworks.qup.service.services.EndUserService;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtUtil {

    private final EndUserService endUserService;

    private String secret;
    private int jwtExpirationInMs;

    @Value("${qup.service.jwt.secret: secret}")
    public void setSecret(String secret) {
        this.secret = secret;
    }

    @Value("${qup.service.jwt.expiration-period-milli-seconds: 500}")
    public void setJwtExpirationInMs(int jwtExpirationInMs) {
        this.jwtExpirationInMs = jwtExpirationInMs;
    }

    public String generateToken(UserDetails userDetails) throws NotFoundRestApiException {
        Map<String, Object> claims = new HashMap<>();

        String username = userDetails.getUsername();

        EndUserDto endUserDto = endUserService.getUserDtoByUserReference(username);

        Collection<? extends GrantedAuthority> roles = userDetails.getAuthorities();

        Boolean isAdmin = roles.contains(new SimpleGrantedAuthority(Role.ADMIN.name()));

        claims.put("isAdmin",isAdmin);
        claims.put("userInfo",endUserDto);

        return doGenerateToken(claims, username);
    }

    private String doGenerateToken(Map<String, Object> claims, String subject) {
        return Jwts.builder().setClaims(claims).setSubject(subject).setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationInMs))
                .signWith(SignatureAlgorithm.HS512, secret).compact();

    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(secret).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException | MalformedJwtException | UnsupportedJwtException | IllegalArgumentException ex) {
            throw new BadCredentialsException("INVALID_CREDENTIALS", ex);
        } catch (ExpiredJwtException ex) {
            throw ex;
        }
    }

    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
        return claims.getSubject();

    }

    public List<SimpleGrantedAuthority> getRolesFromToken(String token) {
        Claims claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();


        Boolean isAdmin = claims.get("isAdmin", Boolean.class);

        if(isAdmin){
            return Collections.singletonList(new SimpleGrantedAuthority(Role.ADMIN.name()));
        }else{
            return Collections.singletonList(new SimpleGrantedAuthority(Role.USER.name()));
        }

    }

}
