package dev.example.test7.controller.mvc;

import dev.example.test7.constant.Route;
import dev.example.test7.entity.User;
import dev.example.test7.exception.custom_exceptions.UploadException;
import dev.example.test7.service.by_entities.UserService;
import dev.example.test7.service.custom_exporters.UserITextPdfExportService;
import dev.example.test7.service.custom_exporters.UserOpenPdfExportService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
@Log4j2
public class PdfController {
    private final UserService userService;
    private final UserOpenPdfExportService userOpenPdfExportService;
    private final UserITextPdfExportService userITextPdfExportService;
//    private final UserPdfImportService userPdfImportService;

    @Autowired
    public PdfController(
            UserService userService,
            UserOpenPdfExportService userPdfExportService,
//            , UserPdfImportService userPdfImportService
            UserITextPdfExportService userITextPdfExportService
    ) {
        this.userService = userService;
        this.userOpenPdfExportService = userPdfExportService;
//        this.userPdfImportService = userPdfImportService;
        this.userITextPdfExportService = userITextPdfExportService;
    }

    @GetMapping("/users/export/open-pdf")
    public void exportUsersToPdfByOpenPdf(
            HttpServletResponse response
    ) {
        String filename = "users_openpdf";
        response.setContentType(MediaType.APPLICATION_PDF_VALUE);
        response.setHeader(
                "Content-Disposition",
                String.format("attachment; filename=\"%s\".pdf", filename)
        );

        List<User> users = userService.findAll();

        try (ServletOutputStream outputStream = response.getOutputStream()) {
            userOpenPdfExportService.exportToOutputStream(users, outputStream);
        } catch (IOException e) {
            throw new UploadException("Could not get outputStream", e);
        }
    }

    @GetMapping("/users/export/itextpdf-pdf")
    public void exportUsersToPdfByITextPdf(
            HttpServletResponse response
    ) {
        String filename = "users_itextpdf";
        response.setContentType(MediaType.APPLICATION_PDF_VALUE);
        response.setHeader(
                "Content-Disposition",
                String.format("attachment; filename=\"%s\".pdf", filename)
        );

        List<User> users = userService.findAll();

        try (ServletOutputStream outputStream = response.getOutputStream()) {
            userITextPdfExportService.exportToOutputStream(users, outputStream);
        } catch (IOException e) {
            throw new UploadException("Could not get outputStream", e);
        }
    }

    @GetMapping("/users/export/extended-openpdf-pdf")
    public ModelAndView exportUsersToPdfByExtendedOpenPdf(
            Model model,
            HttpServletResponse response
    ) {
        model.addAttribute("users", userService.findAll());
        response.setContentType(MediaType.APPLICATION_PDF_VALUE);
        response.setHeader(
                "Content-Disposition",
                "attachment; filename=users.pdf"
        );
        return new ModelAndView("pdfUsersView");
    }
/*

    @PostMapping(
            path = "/users/import/pdf",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ModelAndView importUsersFromPdfToDB(
            @RequestParam("fileImportPdf") MultipartFile file,
            RedirectAttributes redirectAttributes
    ) {
        final List<User> users = userPdfImportService.parseFileToUsersList(file);
        userService.saveList(users);

        redirectAttributes.addFlashAttribute("errorsImportPdf", new ArrayList<>());

        final RedirectView redirectView = new RedirectView(Route.ROUTE_UPLOAD_MULTIPLE_INDEX);
        redirectView.setStatusCode(HttpStatus.MOVED_PERMANENTLY);

        final ModelAndView mav = new ModelAndView();
        mav.setView(redirectView);

        return mav;
    }
*/

    ////////////////////////////////////////
    @ExceptionHandler(UploadException.class)
    public String uploadExceptionHandler(
            UploadException e,
            RedirectAttributes redirectAttributes
    ) {
        log.warn("*** uploadExceptionHandler ***: {}", e.getMessage());

        final ArrayList<String> errors = new ArrayList<>();
        errors.add(e.getMessage());
        redirectAttributes.addFlashAttribute("errorsImportPdf", errors);

        return Route.REDIRECT_UPLOAD_MULTIPLE_INDEX;
    }
}