package dev.example.test7.services;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.Path;
import java.util.Map;

public interface UploadService {
    void init();

    String store(MultipartFile file);

    Map<String, String> getAllStoredFileNamesWithFid();

    URI getFileUriByFid(String fid);

    Path resolveFormatted(String filename);

    Resource getResourceByFid(String uri);

    void writeResourceStreamToOutputFromFile(File file, OutputStream outputStream);

    void copyResourceStreamToOutputFromFile(File file, OutputStream outputStream);

    void deleteAll();
}
