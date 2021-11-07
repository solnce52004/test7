package dev.example.test7.services.spring_exporters;


import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import org.dom4j.DocumentException;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.view.AbstractView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Map;

public abstract class AbstractITextPdfView extends AbstractView {
    public AbstractITextPdfView() {
        setContentType("application/pdf");
    }


    @Override
    protected boolean generatesDownloadContent() {
        return true;
    }

    @Override
    protected final void renderMergedOutputModel(
            @NonNull Map<String, Object> model,
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response
    ) throws Exception {

        // IE workaround: write into byte array first.
        ByteArrayOutputStream baos = createTemporaryOutputStream();

        // Apply preferences and build metadata.
        Document document = newDocument(baos);
        PdfWriter writer = newWriter(document, baos);
        prepareWriter(model, writer, request);
        buildPdfMetadata(model, document, request);

        // Build PDF document.
//        document.open();
        buildPdfDocument(model, document, writer, request, response);
        document.close();

        // Flush to HTTP response.
        writeToResponse(response, baos);
    }

    protected Document newDocument(OutputStream os) {
//        return new Document(PageSize.A4);
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(os));
        return new Document(pdfDoc);
    }

    protected PdfWriter newWriter(
            Document document,
            OutputStream os
    ) throws DocumentException {
//        return PdfWriter.getInstance(document, os);
        return new PdfWriter(os);
    }

    protected void prepareWriter(
            Map<String, Object> model,
            PdfWriter writer,
            HttpServletRequest request
    ) throws DocumentException {
//        writer.setViewerPreferences(getViewerPreferences());
    }

    protected int getViewerPreferences() {
//        return PdfWriter.ALLOW_PRINTING | PdfWriter.PageLayoutSinglePage;
        return 0;
    }

    protected void buildPdfMetadata(
            Map<String, Object> model,
            Document document,
            HttpServletRequest request
    ) {
        //
    }

    protected abstract void buildPdfDocument(
            Map<String, Object> model,
            Document document,
            PdfWriter writer,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws Exception;
}
