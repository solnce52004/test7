package dev.example.config.security.jwt;

import dev.example.config.security.exception.JwtAuthException;
import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;

@PropertySource(value = {"classpath:application.yaml"})
@Component
public class JwtTokenProvider {

    private final UserDetailsService userDetailsService;

    @Value("${jwt.header}")
    public String authHeader;

    @Value("${jwt.secret}")
    public String secretKey;

    @Value("${jwt.expiration}")
    public long expirationInMs;

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
        final Date dateExpiration = new Date(dateNow.getTime() + expirationInMs);

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

        } catch (SignatureException e) {
            throw new JwtAuthException("Invalid JWT signature", e, HttpStatus.UNAUTHORIZED);
        } catch (MalformedJwtException e) {
            throw new JwtAuthException("Invalid JWT token", e, HttpStatus.UNAUTHORIZED);
        } catch (ExpiredJwtException e) {
            throw new JwtAuthException("JWT token is expired", e, HttpStatus.UNAUTHORIZED);
        } catch (UnsupportedJwtException e) {
            throw new JwtAuthException("JWT token is unsupported", e, HttpStatus.UNAUTHORIZED);
        } catch (IllegalArgumentException e) {
            throw new JwtAuthException("JWT claims string is empty", e, HttpStatus.UNAUTHORIZED);
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
        final String authHeader = request.getHeader(this.authHeader);

        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7, authHeader.length());
        }

        return authHeader;
    }
}
