package dev.example.test7.controllers.mvc;

import dev.example.test7.constants.Route;
import dev.example.test7.entities.User;
import dev.example.test7.exceptions.custom_exceptions.UploadException;
import dev.example.test7.services.custom_exporters.UserExcelExportService;
import dev.example.test7.services.custom_exporters.UserExcelImportService;
import dev.example.test7.services.by_entities.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
@Log4j2
public class ExcelController {
    private final UserService userService;
    private final UserExcelExportService userExcelExportService;
    private final UserExcelImportService userExcelImportService;

    @Autowired
    public ExcelController(
            UserService userService,
            UserExcelExportService userExcelExportService,
            UserExcelImportService userExcelImportService
    ) {
        this.userService = userService;
        this.userExcelExportService = userExcelExportService;
        this.userExcelImportService = userExcelImportService;
    }

    @GetMapping("/users/export/excel")
    public void exportUsersToExcel(
            HttpServletResponse response
    ) {
        String filename = "users";
//        response.setContentType("application/vnd.ms-excel");
        response.setContentType("application/octet-stream");
        response.setHeader(
                "Content-Disposition",
                String.format("attachment; filename=\"%s\".xlsx", filename)
        );

        List<User> users = userService.findAll();
//        List<User> users = userService.findAllByName("admin");

        try (ServletOutputStream outputStream = response.getOutputStream()) {
            userExcelExportService.exportToOutputStream(users, outputStream);
        } catch (IOException e) {
            throw new UploadException("Could not get outputStream", e);
        }
    }

    @PostMapping(
            path = "/users/import/excel",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ModelAndView importUsersFromExcelToDB(
            @RequestParam("fileImportExcel") MultipartFile file,
            RedirectAttributes redirectAttributes
    ) {
        final List<User> users = userExcelImportService.parseFileToUsersList(file);
        userService.saveList(users);

        redirectAttributes.addFlashAttribute("errorsImportExcel", new ArrayList<>());

        final RedirectView redirectView = new RedirectView(Route.ROUTE_UPLOAD_MULTIPLE_INDEX);
        redirectView.setStatusCode(HttpStatus.MOVED_PERMANENTLY);

        final ModelAndView mav = new ModelAndView();
        mav.setView(redirectView);

        return mav;
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
        redirectAttributes.addFlashAttribute("errorsImportExcel", errors);

        return "redirect:" + Route.ROUTE_UPLOAD_MULTIPLE_INDEX;
    }
}
