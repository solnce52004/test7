package dev.example.test7.helpers;

public interface UploadFilenameFormatter {
    String formatFilename(String filename);

    String parseToBaseFilename(String filenameFormatted);
}
