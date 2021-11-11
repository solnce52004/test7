package dev.example.test7.service.spring_exporters;

import org.springframework.web.servlet.view.AbstractView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public abstract class AbstractMyCsvView extends AbstractView {
    private static final String CONTENT_TYPE = "text/csv";
    protected static final String DEFAULT_EOL = "\r\n";
    public static final char DEFAULT_DELIMITER = ';';
    public static final char DEFAULT_QUOTE = '"';

    public AbstractMyCsvView() {
        setContentType(CONTENT_TYPE);
    }

    @Override
    protected boolean generatesDownloadContent() {
        return true;
    }

    @Override
    protected final void renderMergedOutputModel(
            Map<String, Object> model,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws Exception {

        response.setContentType(getContentType());
        buildCsvDocument(model, request, response);
    }

    protected abstract void buildCsvDocument(
            Map<String, Object> model,
            HttpServletRequest request,
            HttpServletResponse response
    ) throws Exception;
}
