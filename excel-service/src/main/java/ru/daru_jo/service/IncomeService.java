package ru.daru_jo.service;

import jakarta.annotation.PostConstruct;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.daru_jo.dto.ValCurs;
import ru.daru_jo.entity.CursVal;
import ru.daru_jo.entity.Expenditure;
import ru.daru_jo.entity.Order;
import ru.daru_jo.entity.Revenue;
import ru.daru_jo.helper.ExcelHelper;
import ru.daru_jo.integration.CbrServiceIntegration;
import ru.daru_jo.model.*;

import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class IncomeService {
    private RevenueService revenueService;
    private ExpenditureService expenditureService;

    @Autowired
    public void setRevenueService(RevenueService revenueService) {
        this.revenueService = revenueService;
    }

    @Autowired
    public void setExpenditureService(ExpenditureService expenditureService) {
        this.expenditureService = expenditureService;
    }

    private ParserCSVService service;

    @Autowired
    public void setService(ParserCSVService service) {
        this.service = service;
    }

    private CbrServiceIntegration serviceIntegration;

    @Autowired
    public void setServiceIntegration(CbrServiceIntegration serviceIntegration) {
        this.serviceIntegration = serviceIntegration;
    }

    private ValuteService valuteService;

    @Autowired
    public void setValuteService(ValuteService valuteService) {
        this.valuteService = valuteService;
    }

    @PostConstruct
    public void init() {
        ValCurs valCurs = serviceIntegration.userVacationStart(new Timestamp(System.currentTimeMillis()));
        Order order = service.readDataLineByLine("Daru", "c:\\11\\csv\\eng.csv");
        dump("ss.xlsx", order);
    }

    public void dump(String fileName, Order order) {
        Workbook wb = ExcelHelper.readWorkbookResource("otchet.xlsx");
        dumpDeal(wb, ExcelHelper.createNewList(wb, "Сделки2"), order);
        ExcelHelper.writeWorkbook(wb, fileName);
    }

    private void dumpDeal(Workbook wb, Sheet sheet, Order order) {
        List<Revenue> revenueList = revenueService.findAll(order);
        Map<String, Map<String, Deal>> mapDeal = new LinkedHashMap<>();
        revenueList.forEach(revenue -> {
            Map<String, Deal> dealList = mapDeal.computeIfAbsent(revenue.getCategory(), k -> new LinkedHashMap<>());
            Deal deal = dealList.computeIfAbsent(revenue.getCompanyName(), k -> new Deal(revenue.getCompanyName(), new LinkedList<>(), new LinkedList<>()));
            RevenueModel revenueModel = getRevenueModel(revenue);
            deal.getRevenueList().add(revenueModel);

        });
        expenditureService.findAll(order).forEach(expenditure -> {
            Map<String, Deal> dealList = mapDeal.computeIfAbsent(expenditure.getCategory(), k -> new LinkedHashMap<>());
            Deal deal = dealList.computeIfAbsent(expenditure.getCompanyName(), k -> new Deal(expenditure.getCompanyName(), new LinkedList<>(), new LinkedList<>()));
            ExpenditureModel expenditureModel = getExpenditureModel(expenditure);
            deal.getExpenditureList().add(expenditureModel);

        });
        revenueList.forEach(revenue -> {
            Map<String, Deal> dealList = mapDeal.computeIfAbsent(revenue.getCategory(), k -> new LinkedHashMap<>());
            Deal deal = dealList.computeIfAbsent(revenue.getCompanyName(), k -> new Deal(revenue.getCompanyName(), new LinkedList<>(), new LinkedList<>()));
            ExpenditureModel expenditureModel = getExpenditureModel(revenue);
            deal.getExpenditureList().add(expenditureModel);

        });
        dumpDeal(wb, sheet, mapDeal);
    }


    private void dumpDeal(Workbook wb, Sheet sheet, Map<String, Map<String, Deal>> mapDeal) {
        Row row = sheet.createRow(0);
        Cell cell = row.createCell(0);
        cell.setCellValue("Раздел 1. Доходы от операций с ценными бумагами и иными финансовыми инструментами за период");
        cell.setCellStyle(getCellStyleColor(wb, ExcelHelper.getColor(ColorImp.HEAD), getFont(wb, IndexedColors.WHITE, true, null), HorizontalAlignment.CENTER, VerticalAlignment.CENTER, null));
        sheet.addMergedRegion(new CellRangeAddress(0, 2, 0, 17));
        row = sheet.createRow(3);
        cell = row.createCell(0);
        cell.setCellValue("Обращаем внимание на то, что при отражении сумм комиссии на покупку и продажу ЦБ и ПФИ справочно напротив сумм комиссии в расчетной таблице указано количество. Показатель \"количество\" для комиссии является справочным, т.к. сумма комиссии по покупке указана с учетом пропорционального распределения от общей суммы комиссии на покупку.");
        cell.setCellStyle(getCellStyleColor(wb, null, getFont(wb, (short) 9), HorizontalAlignment.LEFT, VerticalAlignment.CENTER, true));
        sheet.addMergedRegion(new CellRangeAddress(3, 5, 0, 17));
        AtomicInteger rowNum = new AtomicInteger(6);
        mapDeal.forEach((category, dealMap) -> {
            Row rowCat = sheet.createRow(rowNum.get());
            Cell cellComp = rowCat.createCell(0);
            cellComp.setCellValue(category);
            cellComp.setCellStyle(getCellStyleColor(wb, ExcelHelper.getColor(ColorImp.CATEGORY), getFont(wb, true, null), null, VerticalAlignment.CENTER, null));
            sheet.addMergedRegion(new CellRangeAddress(rowNum.get(), rowNum.get(), 0, 17));

            rowNum.set(rowNum.get() + 2);
            ResultRow resultRow = new ResultRow(rowNum.get());
            dumpDeal(wb, sheet, resultRow, dealMap.values().stream().toList());
            createResult(wb, sheet, resultRow.getRow(), 0, "Доход по операциям с " + category, resultRow.getRevenueAmount(), getCellStyleColor(wb, ExcelHelper.getColor(ColorImp.RESULT), getFont(wb, true, (short) 9)));
            createResult(wb, sheet, resultRow.getRow() + 1, 0, "Расход по операциям с " + category, resultRow.getExpenditureAmount(), getCellStyleColor(wb, ExcelHelper.getColor(ColorImp.RESULT), getFont(wb, true, (short) 9)));
            createResult(wb, sheet, resultRow.getRow() + 2, 0, "Итого прибыль/убыток по операциям с " + category, resultRow.getRevenueAmount() - resultRow.getExpenditureAmount(), getCellStyleColor(wb, ExcelHelper.getColor(ColorImp.RESULT), getFont(wb, true, (short) 9)));

            rowNum.set(resultRow.getRow() + 4);
        });
    }
    private ExpenditureModel getExpenditureModel(Revenue revenueCommission) {
        ExpenditureModel expenditureModel = new ExpenditureModel(revenueCommission.getTimestamp(), revenueCommission.getQuantity(), revenueCommission.getCommission(), revenueCommission.getCurrencyCode(), "Основные 1530, 1535", "Комиссия за продажу");
        updateMovement(expenditureModel);
        return expenditureModel;
    }
    private ExpenditureModel getExpenditureModel(Expenditure expenditure) {
        ExpenditureModel expenditureModel = new ExpenditureModel(expenditure.getTimestamp(), expenditure.getPrice(), expenditure.getQuantity(), expenditure.getCurrencyCode(), "Основные 1530, 1535", "Продажа позиции");
        updateMovement(expenditureModel);
        return expenditureModel;
    }

    private RevenueModel getRevenueModel(Revenue revenue) {
        RevenueModel revenueModel = new RevenueModel(revenue.getTimestamp(), revenue.getPrice(), revenue.getQuantity(), revenue.getCurrencyCode(), "Основные 1530, 1535", "Продажа позиции");
        updateMovement(revenueModel);
        return revenueModel;
    }

    private void updateMovement(Movement movement) {
        CursVal cursVal = valuteService.getCursValAnaUpdate(movement.getCurrencyName(), movement.getTimestamp());
        if (cursVal == null) {
            //todo доделать чтобы валюта была
            throw new RuntimeException("Эх цб " + movement.getCurrencyName() + movement.getTimestamp());
        }
        // "currencyCode,course, amount"
        movement.setCurrencyCode(cursVal.getNumCode());
        movement.setCourse(cursVal.getValUnitRate());
        movement.setAmount(movement.getAmountInCur() * cursVal.getValUnitRate());
    }

    private static void dumpDeal(Workbook wb, Sheet sheet, ResultRow resultRow, List<Deal> dealList) {

        for (Deal deal : dealList) {
            dumpDeal(wb, sheet, resultRow, deal);
        }

    }

    private static void dumpDeal(Workbook wb, Sheet sheet, ResultRow resultRow, Deal deal) {
        Integer rowNum = resultRow.getRow();
        Row row = sheet.createRow(rowNum);
        Cell cell = row.createCell(0);
        cell.setCellValue(deal.getCompanyCode());
        cell.setCellStyle(getCellStyleColor(wb, ExcelHelper.getColor(ColorImp.TABLE_HEAD), getFont(wb, true, (short) 9)));
        sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 0, 17));
        rowNum++;

        row = sheet.createRow(rowNum);
        cell = row.createCell(0);
        cell.setCellValue("Выручка");
        cell.setCellStyle(getCellStyleColor(wb, ExcelHelper.getColor(ColorImp.REVENUE), getFont(wb, (short) 9)));
        sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 0, 8));

//        row = sheet.createRow(rowNum);
        cell = row.createCell(9);
        cell.setCellValue("Расход");
        cell.setCellStyle(getCellStyleColor(wb, ExcelHelper.getColor(ColorImp.EXPENDITURE), getFont(wb, (short) 9)));
        sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 9, 17));
        rowNum++;
        createHead(sheet, rowNum, 0, new String[]{"Дата", "Цена", "Кол-во", "В валюте", "Валюта", "Курс", "В рублях", "Код", "Вид дохода"}, getCellStyleColor(wb, ExcelHelper.getColor(ColorImp.COLUMN_HEAD), getFont(wb, (short) 9)));

        createHead(sheet, rowNum, 9, new String[]{"Дата", "Цена", "Кол-во", "В валюте", "Валюта", "Курс", "В рублях", "Код", "Вид дохода"}, getCellStyleColor(wb, ExcelHelper.getColor(ColorImp.COLUMN_HEAD), getFont(wb, (short) 9)));
        rowNum++;
        int colRow = getColRow(deal);
//        for (int i = 0; i < colRow; i++) {
//            sheet.createRow(rowNum);
//        }

        double amountRevenue = createRows(wb, sheet, rowNum, 0, deal.getRevenueList());
        double amountExpenditure = createRows(wb, sheet, rowNum, 9, deal.getExpenditureList());
        rowNum = rowNum + colRow;

        createResult(wb, sheet, rowNum, 0, "Итого доход по операции", amountRevenue, getCellStyleColor(wb, ExcelHelper.getColor(ColorImp.REVENUE_RESULT), getFont(wb, true, (short) 9)));
        createResult(wb, sheet, rowNum++, 9, "Итого расход по операции", amountExpenditure, getCellStyleColor(wb, ExcelHelper.getColor(ColorImp.EXPENDITURE_RESULT), getFont(wb, true, (short) 9)));
        rowNum = rowNum + 1;

        createResult(wb, sheet, rowNum++, 0, "Итого прибыль/убыток по позиции", amountRevenue - amountExpenditure, getCellStyleColor(wb, ExcelHelper.getColor(ColorImp.TABLE_RESULT), getFont(wb, true, (short) 9)));
        resultRow.setRow(rowNum + 1);
        resultRow.setRevenueAmount(resultRow.getRevenueAmount() + amountRevenue);
        resultRow.setExpenditureAmount(resultRow.getExpenditureAmount() + amountExpenditure);
    }

    private static int getColRow(Deal deal) {
        int colRow;
        if (deal.getRevenueList() == null) {
            if (deal.getExpenditureList() == null) {
                colRow = 0;
            } else {
                colRow = deal.getExpenditureList().size();
            }
        } else {
            if (deal.getExpenditureList() == null) {
                colRow = deal.getRevenueList().size();
            } else {
                colRow = Math.max(deal.getRevenueList().size(), deal.getExpenditureList().size());
            }
        }
        return colRow;
    }

    private static void createHead(Sheet sheet, Integer rowNum, Integer cellStart, String[] textList, CellStyle cellStyle) {
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

    private static Double createRows(Workbook wb, Sheet sheet, Integer rowNum, int cellStart, List<? extends Movement> movementList) {

        double amountAll = 0f;
        for (Movement movement : movementList) {
            int cellStartRow = cellStart;
            Row row = sheet.getRow(rowNum);
            if (row == null) {
                row = sheet.createRow(rowNum);
            }
            addCell(wb, row, cellStartRow, movement.getTimestamp());
            cellStartRow++;

            addCell(wb,row, cellStartRow, movement.getPrice(), getFontStyle(wb, (short) 9));
            cellStartRow++;

            addCell(row, cellStartRow, movement.getQuantity(), getFontStyle(wb, (short) 9));
            cellStartRow++;

            addCell(wb,row, cellStartRow, movement.getAmountInCur(), getFontStyle(wb, (short) 9));
            cellStartRow++;

            addCell(row, cellStartRow, movement.getCurrencyCode() + "/" + movement.getCurrencyName(), getFontStyle(wb, (short) 9));
            cellStartRow++;

            addCell(wb,row, cellStartRow, movement.getCourse(), getFontStyle(wb, (short) 9));
            cellStartRow++;

            addCell(wb,row, cellStartRow, movement.getAmount(), getFontStyle(wb, (short) 9));
            cellStartRow++;

            addCell(row, cellStartRow, movement.getCode(), getFontStyle(wb, (short) 9));
            cellStartRow++;

            addCell(row, cellStartRow, movement.getTypeOf(), getCellStyleColor(wb,getFont(wb, (short) 9),true));


            amountAll = amountAll + movement.getAmount();
            rowNum++;
        }
        return amountAll;

    }

    private static void createResult(Workbook wb, Sheet sheet, Integer rowNum, Integer cellStart, String text, double amount, CellStyle style) {
        Row row = sheet.getRow(rowNum);
        if (row == null)
            row = sheet.createRow(rowNum);
        addCell(row, cellStart, text, style);
        sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, cellStart, cellStart + 5));
        addCell(wb, row, cellStart + 6, amount, style);
        sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, cellStart + 6, cellStart + 6 + 2));

    }

    private static void addCell(Workbook wb, Row row, Integer cellStart, Date timestamp) {
        Cell cell = row.createCell(cellStart);
        cell.setCellValue(timestamp);
        CellStyle cellStyle = wb.createCellStyle();
        cellStyle.setFont(getFont(wb, (short) 9));
        cellStyle.setDataFormat(wb.getCreationHelper().createDataFormat().getFormat("dd.mm.yyyy"));
        cell.setCellStyle(cellStyle);
    }


    private static void addCell(Workbook wb, Row row, Integer cellStart, Double amount, CellStyle style) {
        if (amount == null)
            return;
        Cell cell = row.createCell(cellStart);
        DataFormat dataFormat = wb.createDataFormat();
        style.setDataFormat(dataFormat.getFormat("0.##"));
        cell.setCellStyle(style);

        cell.setCellValue(amount);
    }


    private static void addCell(Row row, int cellStart, Integer integer, CellStyle style) {
        Cell cell = row.createCell(cellStart);
        cell.setCellStyle(style);
        cell.setCellValue(integer);
    }

    private static void addCell(Row row, Integer cellStart, String text, CellStyle style) {
        Cell cell = row.createCell(cellStart);
        cell.setCellStyle(style);
        cell.setCellValue(text);
    }

    private static CellStyle getFontStyle(Workbook wb, Short height) {
        return getCellStyleColor(wb, getFont(wb, null, null, height));
    }

    private static Font getFont(Workbook wb, Short height) {
        return getFont(wb, null, height);
    }

    private static Font getFont(Workbook wb, Boolean bold, Short height) {
        return getFont(wb, null, bold, height);
    }

    private static Font getFont(Workbook wb, IndexedColors colorNumText, Boolean bold, Short height) {
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

    private static CellStyle getCellStyleColor(Workbook wb, Font font) {
        return getCellStyleColor(wb, null, font, null, null, null);
    }

    private static CellStyle getCellStyleColor(Workbook wb, Font font,Boolean wrapText) {
        return getCellStyleColor(wb, null, font, null, null, wrapText);
    }

    private static CellStyle getCellStyleColor(Workbook wb, Color color, Font font) {
        return getCellStyleColor(wb, color, font, null, null, null);
    }

    private static CellStyle getCellStyleColor(Workbook wb, Color color, Font font, HorizontalAlignment horizontalAlignment, VerticalAlignment verticalAlignment, Boolean wrapText) {
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
}
