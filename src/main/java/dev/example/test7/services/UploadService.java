package dev.example.test7.services;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedInputStream;
import java.io.File;
import java.net.URI;
import java.nio.file.Path;
import java.util.Map;

public interface UploadService {
    void init();

    String store(MultipartFile file);

    Map<String, String> getAllStoredFileNamesWithFid();

    URI getFileUriByFid(String fid);

    Path resolveFormatted(String filename);

    Resource getAsResource(String filename);

    Resource getResourceByFid(String uri);

    void getResourceStreamByFileAndResponse(File file, HttpServletResponse response);

    void deleteAll();
}
