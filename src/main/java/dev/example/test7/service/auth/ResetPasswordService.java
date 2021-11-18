package dev.example.test7.service.auth;

import dev.example.config.mail.MailService;
import dev.example.config.security.exception.JwtAuthException;
import dev.example.config.security.jwt.JwtTokenProvider;
import dev.example.test7.entity.User;
import dev.example.test7.exception.custom_exceptions.auth.ResetPasswordException;
import dev.example.test7.exception.custom_exceptions.auth.SendResetPasswordException;
import dev.example.test7.service.by_entities.UserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ResetPasswordService {
    private final UserService userService;
    private final MailService mailService;
    private final JwtTokenProvider jwtTokenProvider;

    public User sendResetPasswordToken(String email) throws SendResetPasswordException {
        if (email == null || email.isEmpty()) {
            throw new SendResetPasswordException("Уточните почту, либо зарегистрируйтесь");
        }

        final User user = userService
                .findByEmail(email)
                .orElse(null);

        if (user == null) {
            throw new SendResetPasswordException("Пользователь с указанной почтой не найден: "
                    + email
                    + ". Уточните почту, либо зарегистрируйтесь");
        }

        //выдаем новый reset_token
        user.setResetToken(
                jwtTokenProvider.createVerifyToken(
                        user.getUsername(),
                        user.getPassword()));
        final User savedUser = userService.save(user);
        sendVerifyResetPasswordMessage(user);

        return savedUser;
    }

    private void sendVerifyResetPasswordMessage(User user) {
        String subject = "Сброс пароля";

        Object link = String.format(
                "http://localhost:8080/auth/reset-password/verify/%s",
                user.getResetToken()
        );

        String msg = String.format(
                "Здравствуйте, %s! \n " +
                        "Чтобы сбросить пароль, перейдите по ссылке \n %s\n " +
                        "Ссылка действительна в течении 15 минут.",
                user.getUsername(),
                link
        );
        mailService.send(user.getEmail(), subject, msg);
    }

    public User verifyResetPasswordToken(String token) throws ResetPasswordException {
        verifyJwtToken(token);

        final String email = jwtTokenProvider.getUsername(token);
        final User user = userService.findByEmail(email).orElse(null);

        if (user == null) {
            throw new ResetPasswordException("Пользователь с указанной почтой не найден: " + email);
        }

        if (user.getResetToken() != null && !user.getResetToken().equals(token)) {
            throw new ResetPasswordException("На указанную почту было выслано письмо с другой ссылкой для сброса пароля. Проверьте свою почту, либо отправьте новый запрос на ссылку.");
        }

        return user;
    }

    private void verifyJwtToken(String token) throws ResetPasswordException {
        final boolean validateToken;
        try {
            validateToken = jwtTokenProvider.validateToken(token);
        } catch (JwtAuthException e) {
            throw new ResetPasswordException("Токен не действительный");
        }

        if (token == null || !validateToken) {
            throw new ResetPasswordException("Токен не валидный");
        }
    }

    public void updatePassword(String email, String password) throws ResetPasswordException {
        final User user = userService.findByEmail(email).orElse(null);

        if (user == null) {
            throw new ResetPasswordException("Пользователь с указанной почтой не найден: " + email);
        }

        user.setPassword(password);
        user.setResetToken(null);
        userService.save(user);
    }
}

