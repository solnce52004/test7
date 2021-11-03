package dev.example.test7.exporters;

import dev.example.test7.entities.User;
import org.apache.poi.hssf.usermodel.HSSFDataFormat;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.util.CellUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class UserExcelExporter {
    private final List<User> users;
    private final XSSFWorkbook workbook;
    private final XSSFSheet sheet;
    private final ArrayList<String> headers;

    public UserExcelExporter(List<User> users) {
        this.users = users;
        workbook = new XSSFWorkbook();
        sheet = workbook.createSheet("list1");
        headers = new ArrayList<>();
        headers.add("id");
        headers.add("name");
        headers.add("password");
        headers.add("email");
        headers.add("is_remember_me");
        headers.add("created_at");
        headers.add("updated_at");
    }

    private void writeHeaderRows() {
        final XSSFRow rowHead = sheet.createRow(0);
//        sheet.createFreezePane(1, 1);

        for (int i = 0; i < headers.size(); i++) {
            final XSSFCell cell = rowHead.createCell(i);
            cell.setCellType(CellType.STRING);
            cell.setCellValue(headers.get(i));
        }
    }

    private void writeDataRows() {
        for (int row = 1; row < users.size(); row++) {
            final XSSFRow dataRow = sheet.createRow(row);
            final User user = users.get(row);

            final XSSFCell cellId = dataRow.createCell(0);
            cellId.setCellType(CellType.NUMERIC);
            cellId.setCellValue(user.getId());

            final XSSFCell cellName = dataRow.createCell(1);
            cellName.setCellType(CellType.STRING);
            cellName.setCellValue(user.getName());

            final XSSFCell cellPass = dataRow.createCell(2);
            cellPass.setCellType(CellType.STRING);
            cellPass.setCellValue(user.getPassword());

            final XSSFCell cellEmail = dataRow.createCell(3);
            cellEmail.setCellType(CellType.STRING);
            cellEmail.setCellValue(user.getEmail());

            final XSSFCell cellIsRem = dataRow.createCell(4);
            cellIsRem.setCellType(CellType.NUMERIC);
            cellIsRem.setCellValue(user.getIsRememberMe());

            final XSSFCell cellCr = dataRow.createCell(5);
//            cellCr.setCellType(CellType.STRING);
            cellCr.setCellValue(user.getCreatedAt());
            CellUtil.setCellStyleProperty(
                    cellCr,
                    CellUtil.DATA_FORMAT,
                    HSSFDataFormat.getBuiltinFormat("yyyy-MM-dd HH:mm:ss")
            );

            final XSSFCell cellUp = dataRow.createCell(6);
//            cellUp.setCellType(CellType.STRING);
            cellUp.setCellValue(user.getCreatedAt());
            CellUtil.setCellStyleProperty(
                    cellCr,
                    CellUtil.DATA_FORMAT,
                    HSSFDataFormat.getBuiltinFormat("yyyy-MM-dd HH:mm:ss")
            );
        }
    }

    public void exportToOutputStream(OutputStream outputStream) {
        writeHeaderRows();
        writeDataRows();

        try {
            workbook.write(outputStream);
            workbook.close();
        } catch (IOException e) {
            e.printStackTrace();//add custom ex
        }
    }
}
