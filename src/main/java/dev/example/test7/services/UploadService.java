package dev.example.test7.services;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.stream.Stream;

public interface UploadService {
    void init();

    String store(MultipartFile file);

    Stream<Path> getAll();

    Path resolveFormatted(String filename);

    Resource getAsResource(String filename);

    void deleteAll();
}
