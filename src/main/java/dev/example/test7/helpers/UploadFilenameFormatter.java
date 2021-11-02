package dev.example.test7.helpers;

public interface UploadFilenameFormatter {
    String formatFilenameByRandomFid(String filename);

    String formatFilenameByExistFid(String filename, String fid);

    String parseBaseFilenameByFormattedFilename(String filenameFormatted);

    String parseFidByFormattedFilename(String filenameFormatted);
}
