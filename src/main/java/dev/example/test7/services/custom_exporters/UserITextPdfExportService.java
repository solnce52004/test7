package dev.example.test7.services.custom_exporters;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;
import dev.example.test7.entities.User;
import dev.example.test7.exceptions.custom_exceptions.UploadException;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.util.List;

@Service
public class UserITextPdfExportService implements BaseExporter {
    @Override
    public void init(List<User> users) {

    }

    @Override
    public void writeHeaderRows() {

    }

    @Override
    public void writeDataRows() {

    }

    @Override
    public void exportToOutputStream(List<User> users, OutputStream outputStream) {

        init(users);

        try {
            PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outputStream));
            Document doc = new Document(pdfDoc);
            Table table = new Table(UnitValue.createPercentArray(8)).useAllAvailableWidth();

            for (int i = 0; i < 16; i++) {
                table.addCell("hi");
            }

            doc.add(table);
            doc.close();

        } catch (Exception e) {
            throw new UploadException("Could not get PdfWriter", e);
        }
    }
}
