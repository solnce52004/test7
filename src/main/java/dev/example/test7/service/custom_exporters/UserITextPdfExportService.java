package dev.example.test7.service.custom_exporters;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import dev.example.test7.entity.User;
import dev.example.test7.exception.custom_exceptions.UploadException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserITextPdfExportService implements BaseExporter {
    private List<User> users;
    private Table table;
    private ArrayList<String> headers;

    @Override
    public void init(List<User> users) {
        this.users = users;
        setHeaderNames();
    }

    private void setHeaderNames() {
        headers = new ArrayList<>();
        headers.add("id");
        headers.add("name");
        headers.add("password");
        headers.add("email");
        headers.add("is_remember_me");
        headers.add("created_at");
        headers.add("updated_at");
    }

    private void setTablePreferences() {
        this.table = new Table(UnitValue.createPercentArray(headers.size()))
                .useAllAvailableWidth();
    }

    @Override
    public void writeHeaderRows() {
        PdfFont font = null;
        try {
            font = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (String header : headers) {
            Cell cell = new Cell().add(
                    new Paragraph(header)
                            .setFont(font)
                            .setFontColor(ColorConstants.WHITE));
            cell.setBackgroundColor(ColorConstants.BLUE);
            cell.setBorder(Border.NO_BORDER);
            cell.setTextAlignment(TextAlignment.CENTER);
            table.addCell(cell);
        }
    }

    @Override
    public void writeDataRows() {
        PdfFont font = null;
        try {
            font = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (User user : users) {
            table.addCell(new Cell()
                    .add(new Paragraph(String.valueOf(user.getId())))
                    .setFont(font)
                    .setFontSize(8.0f)
            );
            table.addCell(new Cell()
                    .add(new Paragraph(user.getName()))
                    .setFont(font)
            );
            table.addCell(new Cell()
                    .add(new Paragraph(user.getPassword()))
                    .setFont(font)
            );
            table.addCell(new Cell()
                    .add(new Paragraph(user.getEmail()))
                    .setFont(font)
            );
            table.addCell(new Cell()
                    .add(new Paragraph(String.valueOf(user.getIsRememberMe())))
                    .setFont(font)
            );
            table.addCell(new Cell()
                    .add(new Paragraph(String.valueOf(user.getCreatedAt())))
                    .setFont(font)
            );
            table.addCell(new Cell()
                    .add(new Paragraph(String.valueOf(user.getUpdatedAt())))
                    .setFont(font)
            );
        }
    }

    @Override
    public void exportToOutputStream(List<User> users, OutputStream outputStream) {

        init(users);

        try {
            PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outputStream));
            Document document = new Document(pdfDoc);

            setTablePreferences();

            writeHeaderRows();
            writeDataRows();

            document.add(table);
            document.close();

        } catch (Exception e) {
            throw new UploadException("Could not get PdfWriter", e);
        }
    }
}
