package dev.example.test7.controller.api;

import dev.example.test7.entity.User;
import dev.example.test7.service.by_entities.UserService;
import dev.example.test7.service.custom_exporters.CsvExportService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Api
@RequestMapping("/api/v1")
public class CsvApiController {
    private final UserService userService;
    private final CsvExportService csvExportService;

    public CsvApiController(
            UserService userService,
            CsvExportService csvExportService
    ) {
        this.userService = userService;
        this.csvExportService = csvExportService;
    }

    @ApiOperation(value = "Экспорт список пользователей в файл формата .csv (с разделителем ';')")
    @GetMapping("/users/export/csv")
    public ResponseEntity<Object> exportUsersToCsv() {

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/csv"));
        headers.add(
                "Content-Disposition",
                "attachment; filename=users.csv"
        );

        final List<User> users = userService.findAll();
        InputStreamResource body = csvExportService.getInputStreamResource(users);

        return ResponseEntity
                .ok()
                .headers(headers)
                .body(body);
    }
}
