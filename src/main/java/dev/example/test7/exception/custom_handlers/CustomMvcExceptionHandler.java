package dev.example.test7.exception.custom_handlers;

import dev.example.test7.constant.Route;
import dev.example.test7.exception.custom_exceptions.auth.SendResetPasswordException;
import dev.example.test7.exception.custom_exceptions.auth.LoginAuthException;
import dev.example.test7.exception.custom_exceptions.auth.RegistrationAuthException;
import dev.example.test7.exception.custom_exceptions.auth.ResetPasswordException;
import dev.example.test7.exception.custom_exceptions.auth.VerifyRegistrationException;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;

@ControllerAdvice
@Log4j2
public class CustomMvcExceptionHandler {

    @ExceptionHandler(RegistrationAuthException.class)
    public String RegistrationAuthExceptionHandler(
            RegistrationAuthException e,
            RedirectAttributes redirectAttributes
    ) {
        log.warn("*** RegistrationAuthException ***: {}", e.getMessage());
        final ArrayList<String> errors = new ArrayList<>();
        errors.add(e.getMessage());
        redirectAttributes.addFlashAttribute("errors", errors);
        return Route.REDIRECT_AUTH_REGISTRATION;
    }

    @ExceptionHandler(LoginAuthException.class)
    public String LoginAuthExceptionHandler(
            LoginAuthException e,
            RedirectAttributes redirectAttributes
    ) {
        log.warn("*** LoginAuthException ***: {}", e.getMessage());
        final ArrayList<String> errors = new ArrayList<>();
        errors.add(e.getMessage());
        redirectAttributes.addFlashAttribute("errors", errors);
        return Route.REDIRECT_AUTH_LOGIN;
    }

    @ExceptionHandler(VerifyRegistrationException.class)
    public String VerifyRegistrationExceptionHandler(
            VerifyRegistrationException e,
            RedirectAttributes redirectAttributes
    ) {
        log.warn("*** VerifyRegistrationException ***: {}", e.getMessage());
        final ArrayList<String> errors = new ArrayList<>();
        errors.add(e.getMessage());
        redirectAttributes.addFlashAttribute("errors", errors);
        return Route.REDIRECT_AUTH_SEND_VERIFY;
    }

    @ExceptionHandler(ResetPasswordException.class)
    public String ResetPasswordExceptionHandler(
            ResetPasswordException e,
            RedirectAttributes redirectAttributes
    ) {
        log.warn("*** ResetPasswordException ***: {}", e.getMessage());
        final ArrayList<String> errors = new ArrayList<>();
        errors.add(e.getMessage());
        redirectAttributes.addFlashAttribute("errors", errors);
        return Route.REDIRECT_AUTH_RESET_PASSWORD;
    }

    @ExceptionHandler(SendResetPasswordException.class)
    public String SendResetPasswordExceptionHandler(
            SendResetPasswordException e,
            RedirectAttributes redirectAttributes
    ) {
        log.warn("*** SendResetPasswordException ***: {}", e.getMessage());
        final ArrayList<String> errors = new ArrayList<>();
        errors.add(e.getMessage());
        redirectAttributes.addFlashAttribute("errors", errors);
        return Route.REDIRECT_AUTH_SEND_RESET_PASSWORD;
    }
}

