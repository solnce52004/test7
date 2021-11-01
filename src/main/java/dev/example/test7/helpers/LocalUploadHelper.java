package dev.example.test7.helpers;

import dev.example.test7.constants.LocalConstant;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.io.Files.getFileExtension;
import static com.google.common.io.Files.getNameWithoutExtension;

@Log4j2
@Component("LocalUploadHelper")
public class LocalUploadHelper implements UploadFilenameFormatter {

    /**
     * FS
     * гаратируем, что дубликатов не будет
     * локально: сохраняем с суффиксом, для фронта - парсим и отдаем первоначальное название
     * <p>
     * для запроса на файловый сервер:
     * в бд будем класть fid в отдельное поле, потом сорибать/разбирать
     * маска будет вида "host:port/fid?filename=filename&domain=domain"
     */
    @Override
    public String formatFilename(String filename) {
        final String extension = getFileExtension(filename);
        String nameWithoutExtension = getNameWithoutExtension(filename);

        return String.format(
                LocalConstant.LOCAL_UPLOADED_FILE_NAME_FORMAT,
                nameWithoutExtension,
                UUID.randomUUID().toString().replaceAll("-", ""),
                extension
        );
    }

    @Override
    public String parseToBaseFilename(String filenameFormatted) {
        final String extension = getFileExtension(filenameFormatted);
        String nameWithoutExtension = getNameWithoutExtension(filenameFormatted);

        Pattern p = Pattern.compile("^(.+)_fid_(.+)$");// скомпилировали регулярное
        Matcher m = p.matcher(nameWithoutExtension);// поисковик в тексте

        return String.format(
                "%s.%s",
                m.replaceAll("$1"),
                extension
        );
    }
}
