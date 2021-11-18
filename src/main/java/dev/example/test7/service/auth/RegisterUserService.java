package dev.example.test7.service.auth;

import dev.example.config.mail.MailService;
import dev.example.config.security.enums.UserStatusEnum;
import dev.example.config.security.jwt.JwtTokenProvider;
import dev.example.test7.dto.UserDTO;
import dev.example.test7.entity.User;
import dev.example.test7.service.by_entities.UserService;
import dev.example.test7.service.factory_dto.UserDTOFactory;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class RegisterUserService {
    private final UserService userService;
    private final MailService mailService;
    private final JwtTokenProvider jwtTokenProvider;

    public RegisterUserService(
            UserService userService,
            MailService mailService,
            JwtTokenProvider jwtTokenProvider
    ) {
        this.userService = userService;
        this.mailService = mailService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public void sendVerifyMessage(User user) {
        String subject = "Подтверждение регистрации";

        Object link = String.format(
                "http://localhost:8080/auth/verify/%s",
                user.getToken()
        );

        String msg = String.format(
                "Здравствуйте, %s! \n Чтобы завершить регистрацию, перейдите по ссылке \n %s",
                user.getUsername(),
                link
        );
        mailService.send(user.getEmail(), subject, msg);
    }

    public void registerAnonymous(UserDTO userDTO) {
        userDTO.setStatus(UserStatusEnum.NOT_CONFIRMED.name());
        final User user = UserDTOFactory.userDTOtoUserRegister(userDTO);

        final User userExist = userService
                .findByEmail(user.getEmail())
                .orElse(null);

        if (userExist != null) {
            throw new SecurityException("user exists: " + user.getEmail());
        }

        user.setToken(jwtTokenProvider.createVerifyToken(
                user.getUsername(),
                user.getPassword()));
        userService.createAnonymousRead(user);
        sendVerifyMessage(user);
    }

    public UserDTO resentVerify(String email) {
        final User user = userService
                .findByEmail(email)
                .orElse(null);

        if (user == null) {
            throw new SecurityException("user is not exists: "
                    + email
                    + ". Заполните форму регистрации, Вы не зарегистрированы");
        } else if (user.getVerifiedAt() != null) {
            throw new SecurityException("Пользователь с указанным email "
                    + email
                    + " уже заргистрирован");
        }

        user.setToken(
                jwtTokenProvider.createVerifyToken(
                        user.getUsername(),
                        user.getPassword()));
        userService.save(user);//выдаем новый токен
        sendVerifyMessage(user);

        return UserDTOFactory.userToUserDTORegister(user);
    }

    public UserDTO verify(String token) {
        if (token == null || !jwtTokenProvider.validateToken(token)) {
            throw new SecurityException("token invalid");
        }

        final String email = jwtTokenProvider.getUsername(token);
        final User user = userService.findByEmail(email).orElse(null);

        if (user == null ||
                (user.getVerifiedAt() == null && !user.getToken().equals(token))
        ) {
            throw new SecurityException("user is not exists with email: " + email);
        }

        if (user.getVerifiedAt() != null) {
            throw new SecurityException("user is already verified");
        }

        user.setStatus(UserStatusEnum.CONFIRMED.name());
        user.setToken(
                jwtTokenProvider.createVerifyToken(
                        user.getUsername(),
                        user.getPassword()));
        user.setVerifiedAt(new Date());//меняем токен и отмечаем время
        userService.createUserRead(user);

        return UserDTOFactory.userToUserDTOVerified(user);
    }
}
