package dev.example.test7.config.security.jwt;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private final UserDetailsService userDetailsService;

    @Value("${jwt.header}")
    public String authHeader = "Authorization";

    @Value("${jwt.secret}")
    public String secretKey = "authsecret";

    @Value("${jwt.expiration}")
    public long expirationInMilliseconds = 604800;

    @Autowired
    public JwtTokenProvider(@Qualifier("userDetailsServiceImpl") UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @PostConstruct
    protected void init() {
        secretKey = Base64
                .getEncoder()
                .encodeToString(
                        secretKey.getBytes(StandardCharsets.UTF_8)
                );
    }

    public String createToken(String username, String role) {
        final Claims claims = Jwts.claims().setSubject(username);
        claims.put("role", role);

        final Date dateNow = new Date();
        final Date dateExpiration = new Date(dateNow.getTime() + (expirationInMilliseconds * 1000));

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(dateNow)
                .setExpiration(dateExpiration)
                .signWith(
                        SignatureAlgorithm.HS256,
                        secretKey
                )
                .compact();
    }

    public boolean validateToken(String token) {
        final Jws<Claims> claimsJws;
        try {
            claimsJws = Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token);

            // НЕ протух?
            return !claimsJws.getBody().getExpiration().before(new Date());

        } catch (JwtException | IllegalArgumentException e) {
            throw new JwtAuthException("JWT token is expired or invalid!", e, HttpStatus.UNAUTHORIZED);
        }
    }


    public String getUsername(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public Authentication getAuthentication(String token) {
        final String username = getUsername(token);
        final UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        return new UsernamePasswordAuthenticationToken(
                userDetails,
                "",
                userDetails.getAuthorities()
        );
    }

    public String resolveToken(HttpServletRequest request){
        return request.getHeader(authHeader);
    }
}
