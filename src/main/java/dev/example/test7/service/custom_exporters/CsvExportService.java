package dev.example.test7.service.custom_exporters;

import dev.example.test7.entity.User;
import dev.example.test7.exception.custom_exceptions.UploadException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

@Service
public class CsvExportService implements BaseExporter {

    private static final String DEFAULT_EOL = "\r\n";
    private static final char DEFAULT_DELIMITER = ';';
    private static final char DEFAULT_QUOTE = '"';

    private CSVPrinter csvPrinter;
    private CSVFormat format;
    private List<User> users;
    private List<String> dataRows;

    @Override
    public void init(List<User> users) {
        this.users = users;
    }

    private void setDocFormat() {
        format = CSVFormat
                .Builder
                .create()
                .setHeader("id", "name", "email")
                .setQuote(DEFAULT_QUOTE)
                .setDelimiter(DEFAULT_DELIMITER)
                .setRecordSeparator(DEFAULT_DELIMITER + DEFAULT_EOL)
                .setIgnoreEmptyLines(true)
                .setAllowMissingColumnNames(true)
                .setAllowDuplicateHeaderNames(true)
                .build();
    }

    @Override
    public void writeHeaderRows() {
        //
    }

    @Override
    public void writeDataRows() {
        for (User user : users) {
            dataRows = Arrays.asList(
                    String.valueOf(user.getId()),
                    user.getName(),
                    user.getEmail()
            );

            try {
                csvPrinter.printRecord(dataRows);
            } catch (IOException e) {
                throw new UploadException("Could not print record data rows in csv", e);
            }
        }
    }

    @Override
    public void exportToOutputStream(List<User> users, OutputStream outputStream) {

        init(users);
        setDocFormat();

        try {
            csvPrinter = new CSVPrinter(new PrintWriter(outputStream), format);

            writeDataRows();

            csvPrinter.flush();
            csvPrinter.close();

        } catch (IOException e) {
            throw new UploadException("Could not print csv", e);
        }
    }
}
