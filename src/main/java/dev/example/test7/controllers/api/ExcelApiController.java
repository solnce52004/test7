package dev.example.test7.controllers.api;

import dev.example.test7.entities.User;
import dev.example.test7.exceptions.custom_exceptions.UploadException;
import dev.example.test7.services.by_entities.UserService;
import dev.example.test7.services.custom_exporters.UserExcelExportService;
import dev.example.test7.services.custom_exporters.UserExcelImportService;
import dev.example.test7.services.upload.FileSystemUploadService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@Api
public class ExcelApiController {
    private final UserService userService;
    private final UserExcelExportService userExcelExportService;
    private final UserExcelImportService userExcelImportService;
    private final FileSystemUploadService fileSystemUploadService;

    @Autowired
    public ExcelApiController(
            UserService userService,
            UserExcelExportService userExcelExportService,
            UserExcelImportService userExcelImportService,
            FileSystemUploadService fileSystemUploadService) {
        this.userService = userService;
        this.userExcelExportService = userExcelExportService;
        this.userExcelImportService = userExcelImportService;
        this.fileSystemUploadService = fileSystemUploadService;
    }

    @ApiOperation(value = "Экспорт пользователей в файл формата .xlsx")
    @GetMapping("/users/export/excel")
    public ResponseEntity<Object> exportUsersToExcel() throws IOException {
        final HttpHeaders headers = new HttpHeaders();
        String filename = "users.xlsx";
//        response.setContentType("application/vnd.ms-excel");
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.set(
                "Content-Disposition",
                String.format("attachment; filename=\"%s\"", filename)
        );

        List<User> users = userService.findAll();
//        List<User> users = userService.findAllByName("admin");

        final File file = fileSystemUploadService.resolveFormatted(filename).toFile();
        final FileSystemResource resource = new FileSystemResource(file);

        try (OutputStream outputStream = resource.getOutputStream()) {
            userExcelExportService.exportToOutputStream(users, outputStream);
        } catch (IOException e) {
            throw new UploadException("Could not get outputStream", e);
        }

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentLength(resource.contentLength())
//                .contentType(MediaType.parseMediaType("application/text"))
                .body(resource);
    }

    @ApiOperation(value = "Импорт пользователей из файл формата .xlsx в базу данных")
    @PostMapping(
            path = "/users/import/excel",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<Object> importUsersFromExcelToDB(
            @RequestParam("fileImportExcel") MultipartFile file
    ) {
        final List<User> users = userExcelImportService.parseFileToUsersList(file);
        userService.saveList(users);

        return new ResponseEntity<>(
                users,
                new HttpHeaders(),
                HttpStatus.MOVED_PERMANENTLY
        );
    }
}
