package dev.example.test7.controller.mvc;

import dev.example.test7.constant.Route;
import dev.example.test7.exception.custom_exceptions.UploadException;
import dev.example.test7.service.by_entities.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;

@Controller
@Log4j2
public class CsvController {

    private final UserService userService;

    public CsvController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/users/export/extended-mycsv-csv")
    public ModelAndView exportUsersToCsv(
            Model model,
            HttpServletResponse response
    ) {
        model.addAttribute("users", userService.findAll());
        response.setContentType("text/csv");
        response.setHeader(
                "Content-Disposition",
                "attachment; filename=users.csv"
        );
        return new ModelAndView("csvUsersView");
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
}
