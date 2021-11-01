package dev.example.test7.exceptions.custom_handlers;

import dev.example.test7.constants.Route;
import dev.example.test7.exceptions.custom_exceptions.UploadException;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;

@ControllerAdvice
@Log4j2
public class CustomMvcExceptionHandler {

    @ExceptionHandler(UploadException.class)
    public String uploadExceptionHandler(
            UploadException e,
            RedirectAttributes redirectAttributes
    ) {
        log.warn("*** uploadExceptionHandler ***: {}", e.getMessage());

        final ArrayList<String> errors = new ArrayList<>();
        errors.add(e.getMessage());
        redirectAttributes.addFlashAttribute("errors", errors);

        return "redirect:" + Route.ROUTE_UPLOAD_INDEX;
    }
}

