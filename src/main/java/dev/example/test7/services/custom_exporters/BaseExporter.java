package dev.example.test7.services.custom_exporters;

import dev.example.test7.entities.User;
import dev.example.test7.exceptions.custom_exceptions.UploadException;
import org.springframework.core.io.InputStreamResource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public interface BaseExporter {
    void init(List<User> users);

    void writeHeaderRows();

    void writeDataRows();

    void exportToOutputStream(List<User> users, OutputStream outputStream);

    default InputStreamResource getInputStreamResource(List<User> users) {

        InputStreamResource body;

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            exportToOutputStream(users, baos);

            ByteArrayInputStream inputStream = new ByteArrayInputStream(baos.toByteArray());
            body = new InputStreamResource(inputStream);

            inputStream.close();
            baos.flush();

        } catch (IOException e) {
            throw new UploadException("Could not get stream", e);
        }

        return body;
    }
}
