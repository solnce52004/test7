package dev.example.config.security.service;

import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
public class OAuth2UserServiceImpl extends DefaultOAuth2UserService {
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        final OAuth2User user = super.loadUser(userRequest);
        return user;
//        return new UserPrincipalImpl.create(user);
    }
}

//@Service
//public class OAuth2UserServiceImpl extends DefaultOAuth2UserService {
//
//    private final UserService userService;
//
//    @Autowired
//    public OAuth2UserServiceImpl(UserService userService) {
//        this.userService = userService;
//    }
//
//    @Override
//    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
//        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);
//
//        try {
//            return processOAuth2User(oAuth2UserRequest, oAuth2User);
//        } catch (Exception | OAuth2AuthenticationProcessingException ex) {
//            // Throwing an instance of AuthenticationException will trigger the OAuth2AuthenticationFailureHandler
//            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
//        }
//    }
//
//    private OAuth2User processOAuth2User(
//            OAuth2UserRequest oAuth2UserRequest,
//            OAuth2User oAuth2User
//    ) throws OAuth2AuthenticationProcessingException {
//
//        final String registrationId = oAuth2UserRequest
//                .getClientRegistration()
//                .getRegistrationId()
//                .toUpperCase();
//
//        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(
//                registrationId,
//                oAuth2User.getAttributes()
//        );
//
//        if (!StringUtils.hasText(oAuth2UserInfo.getEmail())) {
//            throw new OAuth2AuthenticationProcessingException("Email not found from OAuth2 provider");
//        }
//
//        Optional<User> userOptional = userService.findByEmail(oAuth2UserInfo.getEmail());
//        User user = userOptional.orElse(new User());
//        if (userOptional.isPresent()) {
//            user = userOptional.get();
//
////            if (!user.getProvider().equalsIgnoreCase(registrationId)) {
////                throw new OAuth2AuthenticationProcessingException("Looks like you're signed up with " +
////                        user.getProvider() + " account. Please use your " + user.getProvider() +
////                        " account to login.");
////            }
//            user = updateExistingUser(user, oAuth2UserRequest, oAuth2UserInfo);
//
//        } else if (oAuth2UserInfo.getName() != null) {
//            user = registerNewUser(user, oAuth2UserRequest, oAuth2UserInfo);
//        }
//
//        return UserPrincipalImpl.create(user, oAuth2User.getAttributes());
//    }
//
//    private User updateExistingUser(
//            User existingUser,
//            OAuth2UserRequest oAuth2UserRequest,
//            OAuth2UserInfo oAuth2UserInfo
//    ) {
//        existingUser.setUsername(oAuth2UserInfo.getName());
//        existingUser.setProvider(
//                oAuth2UserRequest
//                        .getClientRegistration()
//                        .getRegistrationId()
//                        .toUpperCase());
////        existingUser.setImageUrl(oAuth2UserInfo.getImageUrl());
//
//        return userService.update(existingUser);
//    }
//
//    private User registerNewUser(
//            User user, OAuth2UserRequest oAuth2UserRequest,
//            OAuth2UserInfo oAuth2UserInfo
//    ) {
////        User user = new User();
//        user.setProvider(
//                oAuth2UserRequest
//                        .getClientRegistration()
//                        .getRegistrationId()
//                        .toUpperCase());
//        user.setUsername(oAuth2UserInfo.getName());
//        user.setEmail(oAuth2UserInfo.getEmail());
////        user.setImageUrl(oAuth2UserInfo.getImageUrl());
//
//        return userService.createUserRead(user);
//    }
//
//}
