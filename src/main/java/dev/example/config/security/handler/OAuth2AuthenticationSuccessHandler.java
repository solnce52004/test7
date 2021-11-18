package dev.example.config.security.handler;

import dev.example.config.security.exception.OAuth2AuthenticationProcessingException;
import dev.example.config.security.factory.OAuth2UserInfoFactory;
import dev.example.config.security.model.OAuth2UserInfo;
import dev.example.test7.constant.Route;
import dev.example.test7.entity.User;
import dev.example.test7.service.by_entities.UserService;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@Component
public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final UserService userService;

    public OAuth2AuthenticationSuccessHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {

        final DefaultOAuth2User principal = (DefaultOAuth2User) authentication.getPrincipal();

        final String provider = ((OAuth2AuthenticationToken) authentication)
                .getAuthorizedClientRegistrationId()
                .toUpperCase();

        try {
             processOAuth2User(provider, principal);

        } catch (Exception | OAuth2AuthenticationProcessingException ex) {
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }

        response.sendRedirect(Route.AUTH_SUCCESS);
    }

    private void processOAuth2User(
            String registrationId,
            DefaultOAuth2User oAuth2User
    ) throws OAuth2AuthenticationProcessingException {

        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(
                registrationId,
                oAuth2User.getAttributes()
        );

        if (!StringUtils.hasText(oAuth2UserInfo.getEmail())) {
            throw new OAuth2AuthenticationProcessingException("Email not found from OAuth2 provider");
        }

        Optional<User> userOptional = userService.findByEmail(oAuth2UserInfo.getEmail());
        User user = userOptional.orElse(new User());
        user.setProvider(registrationId);

        if (userOptional.isPresent()) {
            updateExistingUser(user, oAuth2UserInfo);
        } else if (oAuth2UserInfo.getName() != null &&
                (oAuth2UserInfo.getEmail() == null || !oAuth2UserInfo.getEmail().equalsIgnoreCase(registrationId))) {
            registerNewUser(user, oAuth2UserInfo);
        }
    }

    private void updateExistingUser(
            User existingUser,
            OAuth2UserInfo oAuth2UserInfo
    ) {
        existingUser.setUsername(oAuth2UserInfo.getName());
        userService.update(existingUser);
    }

    private void registerNewUser(
            User user,
            OAuth2UserInfo oAuth2UserInfo
    ) {
        user.setUsername(oAuth2UserInfo.getName());
        user.setEmail(oAuth2UserInfo.getEmail());
        userService.createUserRead(user);
    }
}
