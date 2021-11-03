package dev.example.test7.controllers.mvc;

import dev.example.test7.constants.Route;
import dev.example.test7.constants.View;
import dev.example.test7.dto.UserDTO;
import dev.example.test7.entities.User;
import dev.example.test7.exceptions.custom_exceptions.UploadException;
import dev.example.test7.exporters.UserExcelExporter;
import dev.example.test7.helpers.UploadFilenameFormatter;
import dev.example.test7.services.UploadService;
import dev.example.test7.services.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Controller
@Log4j2
public class FileUploadMultipleController {

    private final UploadService uploadService;
    private final UploadFilenameFormatter uploadFilenameFormatter;
    private final UserService userService;

    @Autowired
    public FileUploadMultipleController(UploadService uploadService, UploadFilenameFormatter uploadFilenameFormatter, UserService userService) {
        this.uploadService = uploadService;
        this.uploadFilenameFormatter = uploadFilenameFormatter;
        this.userService = userService;
    }

    @GetMapping(Route.ROUTE_UPLOAD_MULTIPLE_INDEX)
    public String uploadMultipleIndex(Model model) {

        if (!model.containsAttribute("file")) {
            model.addAttribute(
                    "file",
                    new ArrayList<>()
            );
        }

        if (!model.containsAttribute("uploadedFiles")) {
            model.addAttribute(
                    "uploadedFiles",
                    new ArrayList<>()
            );
        }

        if (!model.containsAttribute("storedFileNamesWithUri")) {
            model.addAttribute(
                    "storedFileNamesWithUri",
                    uploadService.getAllStoredFileNamesWithFid()
            );
        }

        if (!model.containsAttribute("errors")) {
            model.addAttribute(
                    "errors",
                    new ArrayList<>()
            );
        }

        return View.VIEW_UPLOAD_MULTIPLE_INDEX;
    }

    @PostMapping(
            path = Route.ROUTE_UPLOAD_FILE_MULTIPLE,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ModelAndView uploadFileMultiple(
            @RequestParam("file") MultipartFile[] files,
            ModelAndView model,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        final ArrayList<Object> uploadedFiles = model.getModelMap().containsAttribute("uploadedFiles")
                ? (ArrayList<Object>) model.getModelMap().getAttribute("uploadedFiles")
                : new ArrayList<>();

        if (files != null && files.length > 0){
            for (MultipartFile file: files){
                String filename = uploadService.store(file);
                log.info(filename);
                uploadedFiles.add(filename);
            }
        }

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.file", bindingResult);
        }
        redirectAttributes.addFlashAttribute("file", files);
        redirectAttributes.addFlashAttribute("uploadedFiles", uploadedFiles);
        redirectAttributes.addFlashAttribute("errors", new ArrayList<>());

        final RedirectView redirectView = new RedirectView(Route.ROUTE_UPLOAD_MULTIPLE_INDEX);
        redirectView.setStatusCode(HttpStatus.MOVED_PERMANENTLY);

        final ModelAndView mav = new ModelAndView();
        mav.setView(redirectView);

        return mav;
    }


    @GetMapping(path = "/get-resource-by-fid/{fid}")
    public void getResourceByStreams(
            @PathVariable String fid,
            HttpServletResponse response
    ) {
        final URI uriByFid = uploadService.getFileUriByFid(fid);
        final File file = new File(uriByFid);
        final String filename = uploadFilenameFormatter.parseBaseFilenameByFormattedFilename(file.getName());

        response.setContentType("application/octet-stream");
        response.setHeader(
                "Content-Disposition",
                String.format("attachment; filename=\"%s\"", filename)
        );

        try (ServletOutputStream outputStream = response.getOutputStream()) {

//            uploadService.writeResourceStreamToOutputFromFile(file, outputStream);
            uploadService.copyResourceStreamToOutputFromFile(file, outputStream);

        } catch (IOException e){
            throw new UploadException("Could not get outputStream", e);
        }
    }

    @GetMapping(path = "/delete-uploads")
    public void deleteUploads() {
        uploadService.deleteAll();
    }

    /**
     * 2!!!
     */
    @GetMapping(path = "/get-resource-by-fid/2/{fid}")
    @ResponseBody
    public ResponseEntity<Object> UrlResource(
            @PathVariable String fid
    ) throws IOException {
        final Resource resource = uploadService.getResourceByFid(fid);
        final String filename = uploadFilenameFormatter.parseBaseFilenameByFormattedFilename(resource.getFilename());

        final HttpHeaders headers = new HttpHeaders();
        headers.add(
                "Content-Disposition",
                String.format("attachment; filename=\"%s\"", filename)
        );
        headers.add(
                "Cache-Control",
                "no-cache, no-store, must-revalidate"
        );
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentLength(resource.contentLength())
                .contentType(MediaType.parseMediaType("application/text"))
                .body(resource);
    }


    @GetMapping("/users/export")
    public void exportUsersToExcel(
            HttpServletResponse response
    ){
        String filename = "users.xlsx";
        response.setContentType("application/vnd.ms-excel");
//        response.setContentType("application/octet-stream");
        response.setHeader(
                "Content-Disposition",
                String.format("attachment; filename=\"%s\"", filename)
        );

//        List<User> users = userService.findAll();
        List<User> users = userService.findAllByName("admin");
        UserExcelExporter exporter = new UserExcelExporter(users);

        try (ServletOutputStream outputStream = response.getOutputStream()) {
            exporter.exportToOutputStream(outputStream);
        } catch (IOException e){
            throw new UploadException("Could not get outputStream", e);
        }
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
        redirectAttributes.addFlashAttribute("errors", errors);

        return "redirect:" + Route.ROUTE_UPLOAD_MULTIPLE_INDEX;
    }
}
