package dev.example.test7.services.spring_exporters;

import com.lowagie.text.Font;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import dev.example.test7.entities.User;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.document.AbstractPdfView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.util.List;
import java.util.Map;

@Service
public class SpringPdfViewImpl extends AbstractPdfView {

    @Override
    protected void buildPdfDocument(
            Map<String, Object> model,
            Document document,
            PdfWriter writer,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws Exception {
        final Paragraph listOfUsers = new Paragraph("List of users");
        listOfUsers.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(listOfUsers);

        final PdfPTable table = new PdfPTable(7);

        final List<User> users = (List<User>) model.get("users");

        final PdfPCell cell = new PdfPCell();
        final Font font = FontFactory.getFont(FontFactory.HELVETICA);
        font.setColor(Color.black);
        font.setSize(8);

        for (User user : users) {
            cell.setPhrase(new Phrase(String.valueOf(user.getId()), font));
            table.addCell(cell);
        }
        document.add(table);
    }
}
