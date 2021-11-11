package dev.example.test7.controller.domain;

import dev.example.test7.entity.User;
import dev.example.test7.exception.custom_exceptions.UploadException;
import dev.example.test7.service.by_entities.UserService;
import dev.example.test7.service.custom_exporters.UserExcelExportService;
import dev.example.test7.service.upload.FileSystemUploadService;
import org.springframework.stereotype.Controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.List;

@Controller
public class UploadController {
    private final UserService userService;
    private final UserExcelExportService userExcelExportService;
    private final FileSystemUploadService fileSystemUploadService;

    public UploadController(
            UserService userService,
            UserExcelExportService userExcelExportService,
            FileSystemUploadService fileSystemUploadService
    ) {
        this.userService = userService;
        this.userExcelExportService = userExcelExportService;
        this.fileSystemUploadService = fileSystemUploadService;
    }

    public String exportAndSaveUsersToExcelFile(String filename) {
        List<User> users = userService.findAll();
//        List<User> users = userService.findAllByName("admin");

        final Path pathFormatted = fileSystemUploadService
                .resolveFormatted(filename + ".xlsx");
        final String newFileName = pathFormatted.getFileName().toString();
        final File file = pathFormatted.toFile();

        try (OutputStream outputStream = new FileOutputStream(file)) {
            userExcelExportService.exportToOutputStream(users, outputStream);
        } catch (IOException e) {
            throw new UploadException("Could not get outputStream", e);
        }

        return newFileName;
    }
}
