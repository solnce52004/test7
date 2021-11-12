package dev.example.test7.service.custom_exporters;

import dev.example.test7.entity.User;
import dev.example.test7.exception.custom_exceptions.UploadException;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class UserExcelExportService implements BaseExporter {
    private List<User> users;
    private XSSFWorkbook workbook;
    private XSSFSheet sheet;
    private final ArrayList<String> headers = new ArrayList<>();
    private int numRow = 0;

    @Override
    public void init(List<User> users) {
        this.users = users;
        this.workbook = new XSSFWorkbook();
        this.sheet = workbook.createSheet("ListOfUsers");
    }

    @Override
    public void writeHeaderRows() {
        headers.add("id");
        headers.add("name");
        headers.add("password");
        headers.add("email");
        headers.add("is_remember_me");
        headers.add("created_at");
        headers.add("updated_at");

        final XSSFRow rowHead = sheet.createRow(numRow);
//        sheet.createFreezePane(1, 29, 0, 1);//зафиксирован 1 столбец
        sheet.createFreezePane(7, 1, 0, 1);//зафиксирована шапка

        for (int i = 0; i < headers.size(); i++) {
            final XSSFCell cell = rowHead.createCell(i);
            cell.setCellType(CellType.STRING);
            cell.setCellValue(headers.get(i));
        }
    }

    @Override
    public void writeDataRows() {
        final XSSFCreationHelper helper = workbook.getCreationHelper();
        final XSSFCellStyle styleDate = workbook.createCellStyle();
        styleDate.setDataFormat(
                helper.createDataFormat().getFormat("yyyy-MM-dd HH:mm:ss")
        );

        final XSSFCellStyle styleWrap = workbook.createCellStyle();
        styleWrap.setWrapText(true);

        numRow++;

        for (User value : users) {
            final XSSFRow dataRow = sheet.createRow(numRow);
            numRow++;

            final XSSFCell cellId = dataRow.createCell(0);
            cellId.setCellType(CellType.NUMERIC);
            cellId.setCellValue(value.getId());

            final XSSFCell cellName = dataRow.createCell(1);
            cellName.setCellType(CellType.STRING);
            cellName.setCellValue(value.getUsername());

            final XSSFCell cellPass = dataRow.createCell(2);
            cellPass.setCellType(CellType.STRING);
            cellPass.setCellValue(value.getPassword());

            final XSSFCell cellEmail = dataRow.createCell(3);
            cellEmail.setCellType(CellType.STRING);
            cellEmail.setCellValue(value.getEmail());

            final XSSFCell cellIsRem = dataRow.createCell(4);
            cellIsRem.setCellType(CellType.NUMERIC);
            cellIsRem.setCellValue(123);

            final XSSFCell cellCr = dataRow.createCell(5);
            cellCr.setCellValue(value.getCreatedAt());
            cellCr.setCellStyle(styleDate);
            //настройка ниже не работает
//            CellUtil.setCellStyleProperty(
//                    cellCr,
//                    CellUtil.DATA_FORMAT,
//                    HSSFDataFormat.getBuiltinFormat("yyyy-MM-dd HH:mm:ss")
//            );

            final XSSFCell cellUp = dataRow.createCell(6);
            cellUp.setCellValue(value.getCreatedAt());
            cellUp.setCellStyle(styleDate);
        }

        //свернуть строки
//        sheet.groupRow(2, 5);
//        sheet.setRowGroupCollapsed(2, true);

        //свернуть колонки
//        sheet.groupColumn(0, 3);
//        sheet.setColumnGroupCollapsed(0, true);
/*
        // выпадающий список
        String [] list = {"Neonsoft", "Huaxin", "SAP", "Haihui"};
        final XSSFRow listRow = sheet.createRow(numRow);
        final XSSFCell listCell = listRow.createCell(0);
        listCell.setCellValue ("Пожалуйста, выберите");

        DataValidationHelper validationHelper = sheet.getDataValidationHelper();
        CellRangeAddressList regions = new CellRangeAddressList(0,numRow,0,0);
        DataValidationConstraint constraint = validationHelper.createExplicitListConstraint(list);
        DataValidation  dataValidation = validationHelper.createValidation(constraint, regions);
        dataValidation.setSuppressDropDownArrow(true);
        sheet.addValidationData(dataValidation);
        */
    }

    @Override
    public void exportToOutputStream(List<User> users, OutputStream outputStream) {
        init(users);
        writeHeaderRows();
        writeDataRows();

        try {
            workbook.write(outputStream);
            workbook.close();

        } catch (IOException e) {
            throw new UploadException("Could not write to outputStream", e);
//            e.printStackTrace();//add custom ex
        }
    }
}
