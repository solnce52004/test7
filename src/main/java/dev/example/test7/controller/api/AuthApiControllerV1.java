package dev.example.test7.controller.api;

import dev.example.config.security.jwt.JwtTokenProvider;
import dev.example.config.security.dto.AuthRequestDTO;
import dev.example.config.security.dto.JwtResponseDTO;
import dev.example.config.security.dto.TokenRefreshRequestDTO;
import dev.example.config.security.dto.TokenRefreshResponseDTO;
import dev.example.config.security.entity.RefreshToken;
import dev.example.test7.entity.User;
import dev.example.config.security.exception.TokenRefreshException;
import dev.example.config.security.service.RefreshTokenService;
import dev.example.test7.service.by_entities.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthApiControllerV1 {

    //чтобы получить доступ - в SecurityConfig оверрайдим метод authenticationManagerBean()
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;

    public AuthApiControllerV1(
            AuthenticationManager authenticationManager,
            JwtTokenProvider jwtTokenProvider,
            UserService userService,
            RefreshTokenService refreshTokenService
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequestDTO requestDTO) {
        try {
            return ResponseEntity.ok(getResponse(requestDTO));

        } catch (AuthenticationException e) {
            return new ResponseEntity<>("Invalid email or password", HttpStatus.FORBIDDEN);
        }
    }

    private JwtResponseDTO getResponse(AuthRequestDTO requestDTO) {
        //auth
        final UsernamePasswordAuthenticationToken requestToken = new UsernamePasswordAuthenticationToken(
                requestDTO.getEmail(),
                requestDTO.getPassword()
        );
        final Authentication authentication = authenticationManager.authenticate(requestToken);
        final List<String> authorities = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        //get token
        final User user = userService.findByEmail(requestDTO.getEmail()).orElseThrow(
                () -> new UsernameNotFoundException("user doesn't exists")
        );
        final String token = jwtTokenProvider.createToken(
                requestDTO.getEmail(),
                user.getRole().name()
        );
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getId());

        //create response
        return new JwtResponseDTO(
                token,
                refreshToken.getToken(),
                user.getId(),
                user.getName(),
                user.getEmail(),
                authorities
        );
    }

    @PostMapping("/refreshtoken")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody TokenRefreshRequestDTO requestDTO) {

        String requestRefreshToken = requestDTO.getRefreshToken();

        final User user = refreshTokenService.findByToken(requestRefreshToken)
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .orElseThrow(
                        () -> new TokenRefreshException(
                                requestRefreshToken,
                                "Refresh token is not in database!")
                );

        String token = jwtTokenProvider.createToken(
                user.getEmail(),
                user.getRole().name()
        );

        return ResponseEntity.ok(new TokenRefreshResponseDTO(token, requestRefreshToken));
    }

    @PostMapping("/logout")
    public void logout(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        final SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
        logoutHandler.logout(request, response, null);
    }
}
