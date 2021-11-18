package dev.example.test7.service.auth;

import dev.example.config.security.enums.UserStatusEnum;
import dev.example.config.security.jwt.JwtTokenProvider;
import dev.example.test7.dto.UserDTO;
import dev.example.test7.entity.User;
import dev.example.test7.exception.custom_exceptions.auth.RegistrationAuthException;
import dev.example.test7.service.by_entities.UserService;
import dev.example.test7.service.factory_dto.UserDTOFactory;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RegistrationService {
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final VerifyRegistrationService verifyRegistrationService;

    public void registerAnonymous(UserDTO userDTO) throws RegistrationAuthException {
        userDTO.setStatus(UserStatusEnum.NOT_CONFIRMED.name());
        final User user = UserDTOFactory.userDTOtoUserRegister(userDTO);

        final User userExist = userService
                .findByEmail(user.getEmail())
                .orElse(null);

        if (userExist != null) {
            throw new RegistrationAuthException("Пользователь с указанным email уже зарегистрирован: "
                    + user.getEmail()
            + "Уточните email при регистрации, либо авторизуйтесь");
        }

        user.setToken(jwtTokenProvider.createVerifyToken(
                user.getUsername(),
                user.getPassword()));
        userService.createAnonymousRead(user);
        verifyRegistrationService.sendVerifyRegisterMessage(user);
    }
}
