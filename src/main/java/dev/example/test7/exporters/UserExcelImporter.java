package dev.example.test7.exporters;

import dev.example.test7.entities.User;
import dev.example.test7.exceptions.custom_exceptions.UploadException;
import dev.example.test7.services.UserService;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Log4j2
public class UserExcelImporter {

    private final MultipartFile file;
    private final List<User> users = new ArrayList<>();

    public UserExcelImporter(MultipartFile file) {
        this.file = file;
    }

    public List<User> parseFileToUsersList() {
        try (InputStream input = file.getInputStream()) {
//            if (!input.markSupported()) {
//                throw new UploadException("File format not supported");
//            }
            //1
//            final XSSFWorkbook workbook = new XSSFWorkbook(stream);

            //2
            OPCPackage opcPackage = OPCPackage.open(input);
            XSSFWorkbook workbook = new XSSFWorkbook(opcPackage);

            for (int numSheet = 0; numSheet < workbook.getNumberOfSheets(); numSheet++) {
                final XSSFSheet sheet = workbook.getSheetAt(numSheet);
                if (sheet == null) {
                    continue;
                }

                //exclude headers
                for (int numRow = 1; numRow <= sheet.getLastRowNum(); numRow++) {
                    Row row = sheet.getRow(numRow);
                    if (row == null) {
                        continue;
                    }

                    // время создания и обновления проставятся автоматически при добавлении записей в базу
                    // но в академических целях можно попробовать распарсить ячейки с датами

                    final String name = row.getCell(1).getStringCellValue();
                    final String password = row.getCell(2).getStringCellValue();
                    final String email = row.getCell(3).getStringCellValue();
                    final int isRememberMe = (int) row.getCell(4).getNumericCellValue();

                    // validate...
                    if (name.isEmpty() ||
                            password.isEmpty() ||
                            email.isEmpty()
                    ) {
                        continue;
                    }

                    final User user = new User();
                    user.setName(name);
                    user.setPassword(password);
                    user.setEmail(email);
                    user.setIsRememberMe(isRememberMe);

                    users.add(user);
                }
            }

            opcPackage.close();

        } catch (IOException | InvalidFormatException e) {
            throw new UploadException("Can not open input stream");
        }

        if (users.size() == 0) {
            throw new UploadException("No valid users data in file to import");
        }

        return users;
    }
}
