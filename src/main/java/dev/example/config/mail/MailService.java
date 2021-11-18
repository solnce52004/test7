package dev.example.config.mail;

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

    @Autowired
    public MailService(
            JavaMailSender javaMailSender
    ) {
        this.javaMailSender = javaMailSender;
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
