package dev.example.test7.services.custom_exporters;

import dev.example.test7.entities.User;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserPdfImportService implements BaseImporter {
    private final List<User> users = new ArrayList<>();

    @Override
    public List<User> parseFileToUsersList(MultipartFile file) {
        return users;
    }
}
