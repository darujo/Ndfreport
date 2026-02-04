package ru.daru_jo.helper;


import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Consumer;

public class ExcelHelper {
    public static Workbook readWorkbook(String filename) {
        try {
            return new XSSFWorkbook(new File(filename));
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InvalidFormatException e) {
            throw new RuntimeException(e);
        }

    }

    public static Workbook readWorkbookResource(String fileName) {


        try (InputStream in = ExcelHelper.class.getClassLoader().getResourceAsStream(fileName)) {

            return new XSSFWorkbook(Objects.requireNonNull(in));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void writeWorkbook(Workbook wb, String fileName) {
        try {
            FileOutputStream fileOut = new FileOutputStream(fileName);
            wb.write(fileOut);
            fileOut.close();
        } catch (Exception e) {
            //Обработка ошибки
        }
    }

    public static Sheet getList(Workbook wb, String name) {
        return wb.getSheet(name);
    }

    public static Sheet getList(Workbook wb, Integer number) {
        return wb.getSheetAt(number);
    }

    public static Sheet createNewList(Workbook wb, String nameList) {
        return wb.createSheet(nameList);
    }

    public static Row getRow(Sheet sheet, Integer number) {
        return sheet.getRow(number);

    }

    public static void getUpdateRow(Sheet sheet, Consumer<Row> rowUpdater) {
        Iterator<Row> rowIter = sheet.rowIterator();
        while (rowIter.hasNext()) {
            Row row = rowIter.next();
            rowUpdater.accept(row);
        }
    }

    public static Row createNewRow(Sheet sheet, Integer number) {
        return sheet.createRow(number);
    }

    public static Cell getCell(Row row, Integer number) {
        return row.getCell(number);

    }

    public static void getUpdateCell(Row row, Consumer<Cell> cellUpdater) {
        Iterator<Cell> cellIter = row.cellIterator();
        while (cellIter.hasNext()) {
            Cell cell = cellIter.next();
            cellUpdater.accept(cell);
        }
    }

    public static Cell createNewCell(Row row, Integer number) {
        return row.createCell(number);
    }

    public static Color getColor(ColorInterface color) {
        return new XSSFColor(new java.awt.Color(color.getRed(), color.getGreen(), color.getBlue()), null);
    }
}
