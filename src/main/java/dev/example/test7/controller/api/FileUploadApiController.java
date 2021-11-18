package dev.example.test7.controller.api;

import dev.example.test7.constant.Route;
import dev.example.test7.helper.UploadFilenameFormatter;
import dev.example.test7.service.upload.UploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;

@RestController
@RequestMapping("/api/v1")
public class FileUploadApiController {
    private final UploadService uploadService;
    private final UploadFilenameFormatter uploadFilenameFormatter;

    @Autowired
    public FileUploadApiController(UploadService uploadService, UploadFilenameFormatter uploadFilenameFormatter) {
        this.uploadService = uploadService;
        this.uploadFilenameFormatter = uploadFilenameFormatter;
    }

    @PostMapping(
            path = Route.UPLOAD_FILE_MULTIPLE,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Object> uploadFileMultiple(
            @RequestParam("file") MultipartFile[] files
    ) {
        final ArrayList<Object> uploadedFiles = new ArrayList<>();

        if (files != null && files.length > 0) {
            for (MultipartFile file : files) {
                String filename = uploadService.store(file);
                uploadedFiles.add(filename);
            }
        }

        return new ResponseEntity<>(
                uploadedFiles,
                new HttpHeaders(),
                HttpStatus.MOVED_PERMANENTLY
        );
    }

    @GetMapping(path = "/get-resource-by-fid/{fid}")
    @ResponseBody
    public ResponseEntity<Object> getRes(
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
}
