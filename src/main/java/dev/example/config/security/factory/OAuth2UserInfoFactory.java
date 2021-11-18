package dev.example.config.security.factory;

import dev.example.config.security.enums.ProviderEnum;
import dev.example.config.security.exception.OAuth2AuthenticationProcessingException;
import dev.example.config.security.model.GithubOAuth2UserInfo;
import dev.example.config.security.model.GoogleOAuth2UserInfo;
import dev.example.config.security.model.OAuth2UserInfo;

import java.util.Map;

public class OAuth2UserInfoFactory {

    public static OAuth2UserInfo getOAuth2UserInfo(
            String registrationId,
            Map<String, Object> attributes
    ) throws OAuth2AuthenticationProcessingException {

        if(registrationId.equalsIgnoreCase(ProviderEnum.GOOGLE.name())) {
            return new GoogleOAuth2UserInfo(attributes);

        } else if (registrationId.equalsIgnoreCase(ProviderEnum.GITHUB.toString())) {
            return new GithubOAuth2UserInfo(attributes);

        } else {
            throw new OAuth2AuthenticationProcessingException(
                    "Sorry! Login with " + registrationId + " is not supported yet.");
        }
    }
}