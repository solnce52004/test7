package dev.example.test7.service.auth;

import dev.example.config.mail.MailService;
import dev.example.config.security.enums.UserStatusEnum;
import dev.example.config.security.exception.JwtAuthException;
import dev.example.config.security.jwt.JwtTokenProvider;
import dev.example.test7.entity.User;
import dev.example.test7.exception.custom_exceptions.auth.LoginAuthException;
import dev.example.test7.exception.custom_exceptions.auth.RegistrationAuthException;
import dev.example.test7.exception.custom_exceptions.auth.VerifyRegistrationException;
import dev.example.test7.service.by_entities.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@AllArgsConstructor
public class VerifyRegistrationService {
    private final UserService userService;
    private final MailService mailService;
    private final JwtTokenProvider jwtTokenProvider;

    public User resentVerifyRegisterToken(String email) throws VerifyRegistrationException {
        final User user = userService
                .findByEmail(email)
                .orElse(null);

        if (user == null) {
            throw new VerifyRegistrationException("Пользователь с указанным email не найден: "
                    + email
                    + ". Уточните email, либо заполните форму регистрации");

        } else if (user.getVerifiedAt() != null) {
            throw new VerifyRegistrationException("Пользователь с указанным email "
                    + email
                    + " уже заргистрирован. Уточните email, либо авторизуйтесь");
        }

        user.setToken(
                jwtTokenProvider.createVerifyToken(
                        user.getUsername(),
                        user.getPassword()));
        final User savedUser = userService.save(user);//выдаем новый токен
        sendVerifyRegisterMessage(user);

        return savedUser;
    }

    public void sendVerifyRegisterMessage(User user) {
        String subject = "Подтверждение регистрации";

        Object link = String.format(
                "http://localhost:8080/auth/verify/%s",
                user.getToken()
        );

        String msg = String.format(
                "Здравствуйте, %s! \n " +
                        "Чтобы завершить регистрацию, перейдите по ссылке \n %s\n " +
                        "Ссылка действительна в течении 15 минут.",
                user.getUsername(),
                link
        );
        mailService.send(user.getEmail(), subject, msg);
    }

    public User verifyRegisterToken(String token)
            throws
            RegistrationAuthException,
            VerifyRegistrationException,
            LoginAuthException {

        verifyJwtToken(token);

        final String email = jwtTokenProvider.getUsername(token);
        final User user = userService.findByEmail(email).orElse(null);

        if (user == null) {
            throw new RegistrationAuthException("Пользователь с указанным email не найден: "
                    + email
                    + ". Уточните email и заполните форму регистрации");
        }

        if (user.getVerifiedAt() == null &&
                user.getToken() != null &&
                !user.getToken().equals(token)
        ) {
            throw new VerifyRegistrationException("На указанную почту было выслано письмо с другой ссылкой для подтверждения регистрации. Проверьте свою почту, либо отправьте новый запрос на получение ссылки.");
        }

        if (user.getVerifiedAt() != null) {
            throw new LoginAuthException("Вы уже верифицированы, автороизуйтесь");
        }

        user.setStatus(UserStatusEnum.CONFIRMED.name());
        user.setToken(
                jwtTokenProvider.createVerifyToken(
                        user.getUsername(),
                        user.getPassword()));
        user.setVerifiedAt(new Date());//меняем токен и отмечаем время

        return userService.createUserRead(user);
    }

    private void verifyJwtToken(String token) throws VerifyRegistrationException {
        final boolean validateToken;
        try {
            validateToken = jwtTokenProvider.validateToken(token);
        } catch (JwtAuthException e) {
            throw new VerifyRegistrationException("Токен не действительный");
        }

        if (token == null || !validateToken) {
            throw new VerifyRegistrationException("Токен не валидный");
        }
    }
}

