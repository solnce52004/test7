package dev.example.test7.services.custom_exporters;

import dev.example.test7.entities.User;

import java.io.OutputStream;
import java.util.List;

public interface BaseExporter {
    void init(List<User> users);

    void writeHeaderRows();

    void writeDataRows();

    void exportToOutputStream(List<User> users, OutputStream outputStream);
}
