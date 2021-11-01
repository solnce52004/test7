package dev.example.test7.services;

import dev.example.test7.constants.LocalConstant;
import dev.example.test7.exceptions.custom_exceptions.UploadException;
import dev.example.test7.helpers.UploadFilenameFormatter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.*;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

@Service
@Log4j2
public class FileSystemUploadService implements UploadService {

    private static final Path UPLOADS_PATH = Paths.get(LocalConstant.UPLOADS);
    private final UploadFilenameFormatter uploadFilenameFormatter;

    @Autowired
    public FileSystemUploadService(@Qualifier("LocalUploadHelper") UploadFilenameFormatter uploadFilenameFormatter) {
        this.uploadFilenameFormatter = uploadFilenameFormatter;
    }

    @Override
    @PostConstruct
    public void init() {
        if (!Files.isDirectory(UPLOADS_PATH)) {
            try {
                final boolean mkdir = new File(LocalConstant.UPLOADS).mkdir();
                if (!mkdir) {
                    throw new UploadException("Upload dir was not created");
                }
            } catch (Exception e) {
                throw new UploadException("Access to mkdir is denied");
            }
        }
    }

    @Override
    public String store(MultipartFile file) {
        if (file.isEmpty()) {
            throw new UploadException("Failed to store empty file");
        }

        final String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new UploadException("Filename is null");
        }

        String filename = StringUtils.cleanPath(originalFilename);
        log.info(filename);
        if (filename.contains("..")) {
            throw new UploadException("Cannot store file with relative path outside current directory " + filename);
        }

        File newFile = resolveFormatted(filename)
                .toAbsolutePath()
                .toFile();

        try {
            final byte[] fileBytes = file.getBytes();
            try (BufferedOutputStream stream = new BufferedOutputStream(
                    new FileOutputStream(newFile))
            ) {
                stream.write(fileBytes);
                stream.flush();
            }
        } catch (IOException e) {
            throw new UploadException("Failed to store file " + filename, e);
        }

        return filename;
    }

    @Override
    public Stream<Path> getAll() {
        try {
            return Files.walk(UPLOADS_PATH, 1)
                    .filter(path -> !path.equals(UPLOADS_PATH))
                    .map(UPLOADS_PATH::relativize)
                    .map(path -> Paths.get(
                            uploadFilenameFormatter.parseToBaseFilename(path.toFile().getName())
                    ));
        } catch (IOException e) {
            throw new UploadException("Failed to read stored files", e);
        }
    }

    @Override
    public Path resolveFormatted(String filename) {
        final String filenameFormatted = uploadFilenameFormatter.formatFilename(filename);
        return UPLOADS_PATH.resolve(filenameFormatted);
    }

    @Override
    public Resource getAsResource(String filename) {
        try {
            Path file = resolveFormatted(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
//                final String baseFilename = uploadFilenameFormatter.parseToBaseFilename(file.toString());
                return resource;
            } else {
                throw new FileNotFoundException(
                        "Could not read file: " + filename);
            }
        } catch (MalformedURLException | FileNotFoundException e) {
            throw new UploadException("Could not read file: " + filename, e);
        }
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(UPLOADS_PATH.toFile());
    }
}
