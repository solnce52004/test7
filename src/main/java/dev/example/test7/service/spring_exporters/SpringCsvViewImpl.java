package dev.example.test7.service.spring_exporters;

import dev.example.test7.entity.User;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SpringCsvViewImpl extends AbstractMyCsvView {

    @Override
    protected void buildCsvDocument(
            Map<String, Object> model,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws Exception {

        List<User> users = (List<User>) model.get("users");
//        String[] headers = {"id","name"};

        try (CSVPrinter printer = new CSVPrinter(
                response.getWriter(),
                CSVFormat.Builder
                        .create()
                        .setQuote(DEFAULT_QUOTE)
                        .setDelimiter(DEFAULT_DELIMITER)
                        .setRecordSeparator(DEFAULT_DELIMITER + DEFAULT_EOL)
                        .setIgnoreEmptyLines(true)
                        .setAllowMissingColumnNames(true)
                        .setAllowDuplicateHeaderNames(true)
                        .build()
                )
        ) {
            users.forEach(u -> {
                final ArrayList<String> list = new ArrayList<>();
                list.add(String.valueOf(u.getId()));
                list.add(u.getUsername());
                list.add(u.getPassword());
                list.add(u.getEmail());

                try {
                    printer.printRecord(
                            list
                    );
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

        }
    }
}
