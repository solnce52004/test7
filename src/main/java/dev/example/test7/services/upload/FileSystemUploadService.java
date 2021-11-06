package dev.example.test7.services.upload;

import dev.example.test7.constants.LocalConstant;
import dev.example.test7.exceptions.custom_exceptions.UploadException;
import dev.example.test7.helpers.UploadFilenameFormatter;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.IOUtils;
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
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.stream.Collectors;

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

        //transferTo()

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

    /**
     * fid->base name
     */
    @Override
    public Map<String, String> getAllStoredFileNamesWithFid() {
        try {
            return Files.walk(UPLOADS_PATH, 1)
                    .filter(path -> !path.equals(UPLOADS_PATH))
//                    .map(UPLOADS_PATH::relativize)
                    .collect(Collectors.toMap(
                            path -> uploadFilenameFormatter.parseFidByFormattedFilename(path.toFile().getName()),//fid
                            path -> uploadFilenameFormatter.parseBaseFilenameByFormattedFilename(path.toFile().getName())//base name
                    ));
        } catch (IOException e) {
            throw new UploadException("Failed to read stored file names", e);
        }
    }

    @Override
    public URI getFileUriByFid(String fid) {
        try {
            final URI uri = Files.walk(UPLOADS_PATH, 1)
                    .filter(path -> !path.equals(UPLOADS_PATH))
                    .filter(path -> path
                            .getFileName()
                            .toString()
                            .startsWith(fid + "_fid_"))
                    .map(Path::toUri)
                    .findFirst()
                    .orElse(null);
            if (uri == null) {
                throw new UploadException("File not found by fid");
            }
            return uri;
        } catch (IOException e) {
            throw new UploadException("Failed to read stored files", e);
        }
    }

    @Override
    public Path resolveFormatted(String filename) {
        final String filenameFormatted = uploadFilenameFormatter.formatFilenameByRandomFid(filename);
        return UPLOADS_PATH.resolve(filenameFormatted);
    }

    /**
     * +
     */
    @Override
    public Resource getResourceByFid(String fid) {
        final URI uriByFid = getFileUriByFid(fid);

        try {
            Resource resource = new UrlResource(uriByFid);
            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new FileNotFoundException("Could not read file");
            }
        } catch (MalformedURLException | FileNotFoundException e) {
            throw new UploadException("Could not read file", e);
        }
    }

    @Override
    public void writeResourceStreamToOutputFromFile(File file, OutputStream outputStream) {
        try (BufferedInputStream inputStream = new BufferedInputStream(
                Files.newInputStream(Path.of(file.getAbsolutePath()))
        )) {

            byte[] buffer = new byte[8192];
            int bytesRead = -1;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

        } catch (IOException e) {
            throw new UploadException("Could not read file", e);
        }
    }

    public void copyResourceStreamToOutputFromFile(File file, OutputStream outputStream) {
        //FileInputStream is deprecated
//        try(BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(file))) {

        try (BufferedInputStream inputStream = new BufferedInputStream(
                Files.newInputStream(Path.of(file.getAbsolutePath()))
        )) {

            //DEFAULT_BUFFER_SIZE
            IOUtils.copy(inputStream, outputStream);

        } catch (IOException e) {
            throw new UploadException("Could not read file", e);
        }
    }

    @Override
    public void deleteAll() {
        if (Files.isDirectory(UPLOADS_PATH)) {
            try {
                FileSystemUtils.deleteRecursively(UPLOADS_PATH);
            } catch (IOException e) {
                throw new UploadException("Could not delete uploads", e);
            }
        }
    }
}
