package dev.example.test7.helper;

import dev.example.test7.constant.LocalConstant;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    public String formatFilenameByRandomFid(String filename) {
        return String.format(
                LocalConstant.LOCAL_UPLOADED_FILE_NAME_FORMAT,
                UUID.randomUUID().toString().replaceAll("-", ""),
                filename
        );
    }

    @Override
    public String formatFilenameByExistFid(String filename, String fid) {
        return String.format(
                LocalConstant.LOCAL_UPLOADED_FILE_NAME_FORMAT,
                fid,
                filename
        );
    }

    @Override
    public String parseBaseFilenameByFormattedFilename(String filenameFormatted) {
        Pattern p = Pattern.compile("^(.+)_fid_(.+)$");// скомпилировали регулярное
        Matcher m = p.matcher(filenameFormatted);// поисковик в тексте

        return m.replaceAll("$2");
    }

    @Override
    public String parseFidByFormattedFilename(String filenameFormatted) {
        Pattern p = Pattern.compile("^(.+)_fid_(.+)$");
        Matcher m = p.matcher(filenameFormatted);

        return m.replaceAll("$1");
    }
}
