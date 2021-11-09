package dev.example.test7.controllers.api;

import dev.example.test7.config.security.jwt.JwtTokenProvider;
import dev.example.test7.dto.AuthRequestDTO;
import dev.example.test7.entities.User;
import dev.example.test7.services.by_entities.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthApiControllerV1 {

    //чтобы получить доступ - в SecurityConfig оверрайдим метод authenticationManagerBean()
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    public AuthApiControllerV1(
            AuthenticationManager authenticationManager,
            JwtTokenProvider jwtTokenProvider,
            UserService userService
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody AuthRequestDTO requestDTO
    ){
        try {
            Map<String, String> response = getResponse(requestDTO);
            return ResponseEntity.ok(response);

        } catch (AuthenticationException e) {
           return new ResponseEntity<>("Invalid email or password", HttpStatus.FORBIDDEN);
        }
    }

    private Map<String, String> getResponse(AuthRequestDTO requestDTO) {
        //auth
        final UsernamePasswordAuthenticationToken requestToken = new UsernamePasswordAuthenticationToken(
                requestDTO.getEmail(),
                requestDTO.getPassword()
        );
        authenticationManager.authenticate(requestToken);

        //get token
        final User user = userService.findByEmail(requestDTO.getEmail()).orElseThrow(
                () -> new UsernameNotFoundException("user doesn't exists")
        );
        final String token = jwtTokenProvider.createToken(
                requestDTO.getEmail(),
                user.getRole().name()
        );

        //create response
        Map<String, String> response = new HashMap<>();
        response.put("email", requestDTO.getEmail());
        response.put("token", token);
        return response;
    }

    @PostMapping("/logout")
    public void logout(
            HttpServletRequest request,
            HttpServletResponse response
    ){
        final SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
        logoutHandler.logout(request, response, null);
    }
}
