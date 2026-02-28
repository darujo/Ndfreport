package ru.daru_jo.service;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.daru_jo.entity.Order;
import ru.daru_jo.helper.ExcelHelper;
import ru.daru_jo.service.db.OrderService;
import ru.daru_jo.service.export.DumpCoupon;
import ru.daru_jo.service.export.DumpDeal;
import ru.daru_jo.service.export.DumpDividend;

import java.util.*;

@Slf4j
@Service
public class IncomeService {

    private ParserCSVService service;
    private OrderService orderService;

    @Autowired
    public void setService(ParserCSVService service) {
        this.service = service;
    }

    private DumpDeal dumpDeal;
    private DumpCoupon dumpCoupon;
    private DumpDividend dumpDividend;

    @Autowired
    public void setDumpCoupon(DumpCoupon dumpCoupon) {
        this.dumpCoupon = dumpCoupon;
    }

    @Autowired
    public void setDumpDeal(DumpDeal dumpDeal) {
        this.dumpDeal = dumpDeal;
    }

    @Autowired
    public void setDumpDividend(DumpDividend dumpDividend) {
        this.dumpDividend = dumpDividend;
    }

    @PostConstruct
    public void init() {
        try {


            Order order = new Order("Daru");
            orderService.saveOrder(order);
            service.readDataLineByLine(order, "c:\\11\\csv\\eng.csv");
            dump("C:/java/NDFLBroker/report/ss" + order.getId() + ".xlsx", order);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public void dump(String fileName, Order order) {
        Workbook wb = ExcelHelper.readWorkbookResource("otchet.xlsx");
        dumpDeal.dump(wb, ExcelHelper.createNewList(wb, "Сделки2"), order);
        dumpCoupon.dump(wb, ExcelHelper.createNewList(wb, "Купоны2"), order);
        dumpDividend.dump(wb, ExcelHelper.createNewList(wb, "Дивиденты2"), order);
        ExcelHelper.writeWorkbook(wb, fileName);

    }


    public static void createHead(Sheet sheet, Integer rowNum, Integer cellStart, String[] textList, CellStyle cellStyle) {
        Row row = sheet.getRow(rowNum);
        if (row == null)
            row = sheet.createRow(rowNum);
        for (String text : textList) {
            Cell cell = row.createCell(cellStart);
            cell.setCellValue(text);
            cell.setCellStyle(cellStyle);
            cellStart++;
        }

    }

    public static void addCell(Workbook wb, Row row, Integer cellStart, Date timestamp) {
        Cell cell = row.createCell(cellStart);
        cell.setCellValue(timestamp);
        CellStyle cellStyle = wb.createCellStyle();
        cellStyle.setFont(getFont(wb, (short) 9));
        cellStyle.setDataFormat(wb.getCreationHelper().createDataFormat().getFormat("dd.mm.yyyy"));
        cell.setCellStyle(cellStyle);
    }


    public static void addCell(Workbook wb, Row row, Integer cellStart, Double amount, CellStyle style) {
        if (amount == null)
            return;
        Cell cell = row.createCell(cellStart);

        DataFormat dataFormat = wb.createDataFormat();
        style.setDataFormat(dataFormat.getFormat("0.##"));
        cell.setCellStyle(style);

        cell.setCellValue(amount);
    }
    public static void addCellPercent(Workbook wb, Row row, Integer cellStart, Double amount, CellStyle style) {
        if (amount == null)
            return;
        Cell cell = row.createCell(cellStart);

        DataFormat dataFormat = wb.createDataFormat();
        style.setDataFormat(dataFormat.getFormat("0%"));
        cell.setCellStyle(style);

        cell.setCellValue(amount);
    }

    public static void addCell(Row row, int cellStart, Double value, CellStyle style) {
        if(value== null){
            return;
        }
        Cell cell = row.createCell(cellStart);
        cell.setCellStyle(style);
        cell.setCellValue(value);
    }

    public static void addCell(Row row, Integer cellStart, String text, CellStyle style) {
        Cell cell = row.createCell(cellStart);
        cell.setCellStyle(style);
        cell.setCellValue(text);
    }

    public static void addCell(Row row, Integer cellStart, Integer text, CellStyle style) {
        Cell cell = row.createCell(cellStart);
        cell.setCellStyle(style);
        cell.setCellValue(text);
    }

    public static CellStyle getFontStyle(Workbook wb, Short height) {
        return getCellStyleColor(wb, getFont(wb, null, null, height));
    }

    public static Font getFont(Workbook wb, Short height) {
        return getFont(wb, null, height);
    }

    public static Font getFont(Workbook wb, Boolean bold, Short height) {
        return getFont(wb, null, bold, height);
    }

    public static Font getFont(Workbook wb, IndexedColors colorNumText, Boolean bold, Short height) {
        Font font = wb.createFont();
        if (colorNumText != null) {
            font.setColor(colorNumText.getIndex());

        }
        if (bold != null) {
            font.setBold(bold);
        }
        if (height != null) {
            font.setFontHeightInPoints(height);
        }
        font.setFontName("Arial");
        return font;
    }

    public static CellStyle getCellStyleColor(Workbook wb, Font font) {
        return getCellStyleColor(wb, null, font, null, null, null);
    }

    public static CellStyle getCellStyleColor(Workbook wb, Font font, Boolean wrapText) {
        return getCellStyleColor(wb, null, font, null, null, wrapText);
    }

    public static CellStyle getCellStyleColor(Workbook wb, Color color, Font font) {
        return getCellStyleColor(wb, color, font, null, null, null);
    }

    public static CellStyle getCellStyleColor(Workbook wb, Color color, Font font, HorizontalAlignment horizontalAlignment, VerticalAlignment verticalAlignment, Boolean wrapText) {
        CellStyle cellStyle = wb.createCellStyle();
        if (color != null) {
            cellStyle.setFillBackgroundColor(color);
            cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            cellStyle.setFillForegroundColor(color);
        }
        if (font != null) {


            cellStyle.setFont(font);
        }
        if (horizontalAlignment != null) {
            cellStyle.setAlignment(horizontalAlignment);
        }
        if (verticalAlignment != null) {
            cellStyle.setVerticalAlignment(verticalAlignment);
        }
        if (wrapText != null) {
            cellStyle.setWrapText(wrapText);
        }

        return cellStyle;
    }

    @Autowired
    public void setOrderService(OrderService orderService) {
        this.orderService = orderService;
    }
}
