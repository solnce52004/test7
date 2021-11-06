package dev.example.test7.services.custom_exporters;

import dev.example.test7.entities.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface BaseImporter {
    List<User> parseFileToUsersList(MultipartFile file);
}
