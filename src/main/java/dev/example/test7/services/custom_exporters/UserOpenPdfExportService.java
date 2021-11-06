package dev.example.test7.services.custom_exporters;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import dev.example.test7.entities.User;
import dev.example.test7.exceptions.custom_exceptions.UploadException;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class UserOpenPdfExportService implements BaseExporter {
    private List<User> users;
    private PdfPTable table;
    private Document document;
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

    private void setDocumentTitle() {
        final Font font = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        font.setSize(12);
        font.setColor(Color.DARK_GRAY);

        final Paragraph listOfUsers = new Paragraph("List of users", font);
        listOfUsers.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(listOfUsers);
    }

    private void setTablePreferences() {
        this.table = new PdfPTable(headers.size());
        table.setWidthPercentage(100F);
        table.setWidths(new float[]{1.5F, 3.0F, 3.0F, 3.0F, 1.0F, 3.0F, 3.0F});
        table.setSpacingBefore(10);
    }

    @Override
    public void writeHeaderRows() {
        final PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(Color.DARK_GRAY);
        cell.setPadding(5);

        final Font font = FontFactory.getFont(FontFactory.HELVETICA);
        font.setColor(Color.white);
        font.setSize(10);

        for (String header : headers) {
            cell.setPhrase(new Phrase(header, font));
            table.addCell(cell);
        }
    }

    @Override
    public void writeDataRows() {
        final PdfPCell cell = new PdfPCell();

        final Font font = FontFactory.getFont(FontFactory.HELVETICA);
        font.setColor(Color.black);
        font.setSize(8);

        for (User user : users) {
            cell.setPhrase(new Phrase(String.valueOf(user.getId()), font));
            table.addCell(cell);
            cell.setPhrase(new Phrase(user.getName(), font));
            table.addCell(cell);
            cell.setPhrase(new Phrase(user.getPassword(), font));
            table.addCell(cell);
            cell.setPhrase(new Phrase(user.getEmail(), font));
            table.addCell(cell);
            cell.setPhrase(new Phrase(String.valueOf(user.getIsRememberMe()), font));
            table.addCell(cell);
            cell.setPhrase(new Phrase(String.valueOf(user.getCreatedAt()), font));
            table.addCell(cell);
            cell.setPhrase(new Phrase(String.valueOf(user.getUpdatedAt()), font));
            table.addCell(cell);
        }
    }

    @Override
    public void exportToOutputStream(List<User> users, OutputStream outputStream) {

        init(users);
        this.document = new Document(PageSize.A4);

        try {
            PdfWriter.getInstance(document, outputStream);
            document.open();

            setDocumentTitle();
            setTablePreferences();

            writeHeaderRows();
            writeDataRows();

            document.add(table);
            document.close();

        } catch (DocumentException e) {
            throw new UploadException("Could not get Instance", e);
        }
    }
}
