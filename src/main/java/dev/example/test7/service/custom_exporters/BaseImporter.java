package dev.example.test7.service.custom_exporters;

import dev.example.test7.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface BaseImporter {
    List<User> parseFileToUsersList(MultipartFile file);
}
