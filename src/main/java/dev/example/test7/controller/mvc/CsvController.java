package dev.example.test7.controller.mvc;

import dev.example.test7.annotation.auth.PostFilterUsernamesExceptCurrent;
import dev.example.test7.annotation.auth.PreFilterUsernamesExceptCurrent;
import dev.example.test7.constant.Route;
import dev.example.test7.exception.custom_exceptions.UploadException;
import dev.example.test7.service.by_entities.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@Controller
@Log4j2
public class CsvController {

    private final UserService userService;

    public CsvController(UserService userService) {
        this.userService = userService;
    }

    //для использования данной аннотации
    // имеет смысл префикс названия роли!!!! поэтому в базе храним с префиксом!
//    @PreAuthorize("hasAnyRole('ROLE_USER', 'READ')")

    @Secured({"ROLE_USER"})
//    @Secured({"ROLE_ADMIN", "ROLE_USER"})
//    @RolesAllowed(value={"ROLE_ADMIN", "ROLE_USER"})
//    @PostAuthorityWriter
//    @PreAuthorityByRoleReader
    @GetMapping("/users/export/extended-mycsv-csv")
    public ModelAndView exportUsersToCsv(
            Model model,
            HttpServletResponse response
    ) {
//        log.info(getAllUsernamesExceptCurrent());

        model.addAttribute("users", userService.findAll());
        response.setContentType("text/csv");
        response.setHeader(
                "Content-Disposition",
                "attachment; filename=users.csv"
        );
        return new ModelAndView("csvUsersView");
    }

    @PreFilterUsernamesExceptCurrent
    public String joinUsernamesAndRoles(
            List<String> usernames, List<String> roles) {

        return String.join(";", usernames)
                + ":" + String.join(";", roles);
    }

    @PostFilterUsernamesExceptCurrent
    public List<String> getAllUsernamesExceptCurrent() {
        return userService.getAllEmails();
    }


    ////////////////////////////////////////
    @ExceptionHandler(UploadException.class)
    public String uploadExceptionHandler(
            UploadException e,
            RedirectAttributes redirectAttributes
    ) {
        log.warn("*** uploadExceptionHandler ***: {}", e.getMessage());

        final ArrayList<String> errors = new ArrayList<>();
        errors.add(e.getMessage());

        //TODO
//        redirectAttributes.addFlashAttribute("errorsCsv", errors);

        return "redirect:" + Route.ROUTE_UPLOAD_MULTIPLE_INDEX;
    }

    //Можно перехватить тут ошибки доступа
    //Можно перехватить глобальном на уровне (но это будет правильнее для api)
    //можно переопределить свою страницу ошибки, но запрос будет не идемпотентный
    @ExceptionHandler(AccessDeniedException.class)
    public String AccessDeniedExceptionHandler(
            AccessDeniedException e,
            RedirectAttributes redirectAttributes
    ) {
        log.warn("*** AccessDeniedException ***: {}", e.getMessage());

        final ArrayList<String> errors = new ArrayList<>();
        errors.add(e.getMessage());

        //TODO
//        redirectAttributes.addFlashAttribute("errorsCsv", errors);
        redirectAttributes.addFlashAttribute("errors", errors);

        return "redirect:" + Route.ROUTE_UPLOAD_MULTIPLE_INDEX;
    }
}
