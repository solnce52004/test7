package dev.example.config.mail;

import dev.example.config.security.jwt.JwtTokenProvider;
import dev.example.test7.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {
    @Value("${spring.mail.username}")
    private String username;

    private final JavaMailSender javaMailSender;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public MailService(
            JavaMailSender javaMailSender,
            JwtTokenProvider jwtTokenProvider
    ) {
        this.javaMailSender = javaMailSender;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public void send(
            String to,
            String subject,
            String msg
    ) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(username);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(msg);
        javaMailSender.send(message);
    }
}
