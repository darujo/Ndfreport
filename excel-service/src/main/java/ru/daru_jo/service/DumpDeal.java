package ru.daru_jo.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.daru_jo.entity.*;
import ru.daru_jo.helper.ExcelHelper;
import ru.daru_jo.model.*;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class DumpDeal {
    private RevenueService revenueService;
    private ExpenditureService expenditureService;
    private ValuteService valuteService;
    @Autowired
    public void setRevenueService(RevenueService revenueService) {
        this.revenueService = revenueService;
    }

    @Autowired
    public void setExpenditureService(ExpenditureService expenditureService) {
        this.expenditureService = expenditureService;
    }

    @Autowired
    public void setValuteService(ValuteService valuteService) {
        this.valuteService = valuteService;
    }

    public void dump(Workbook wb, Sheet sheet, Order order) {
        List<Revenue> revenueList = revenueService.findAll(order);
        Map<String, Map<String, Deal>> mapDeal = new LinkedHashMap<>();
        revenueList.forEach(revenue -> {
            Map<String, Deal> dealList = mapDeal.computeIfAbsent(revenue.getCategory(), k -> new LinkedHashMap<>());
            Deal deal = dealList.computeIfAbsent(revenue.getCompanyName(), k -> new Deal(revenue.getCompanyName(), new LinkedList<>(), new LinkedList<>()));
            RevenueModel revenueModel = getRevenueModel(revenue);
            deal.getRevenueList().add(revenueModel);

        });
        expenditureService.findAll(order).forEach(expenditure -> dumpMon(expenditure, mapDeal));
        revenueList.forEach(revenue -> dumpMon(revenue, mapDeal));

        Row row = sheet.createRow(0);
        Cell cell = row.createCell(0);
        cell.setCellValue(String.format("Раздел 1. Доходы от операций с ценными бумагами и иными финансовыми инструментами за период 01/01/%s - 31/12/%s ",order.getYear(), order.getYear()));
        cell.setCellStyle(IncomeService.getCellStyleColor(wb, ExcelHelper.getColor(ColorImp.HEAD), IncomeService.getFont(wb, IndexedColors.WHITE, true, null), HorizontalAlignment.CENTER, VerticalAlignment.CENTER, null));
        sheet.addMergedRegion(new CellRangeAddress(0, 2, 0, 17));
        row = sheet.createRow(3);
        cell = row.createCell(0);
        cell.setCellValue("Обращаем внимание на то, что при отражении сумм комиссии на покупку и продажу ЦБ и ПФИ справочно напротив сумм комиссии в расчетной таблице указано количество. Показатель \"количество\" для комиссии является справочным, т.к. сумма комиссии по покупке указана с учетом пропорционального распределения от общей суммы комиссии на покупку.");
        cell.setCellStyle(IncomeService.getCellStyleColor(wb, null, IncomeService.getFont(wb, (short) 9), HorizontalAlignment.LEFT, VerticalAlignment.CENTER, true));
        sheet.addMergedRegion(new CellRangeAddress(3, 5, 0, 17));

        dump(wb, sheet, mapDeal);
    }

    private void dumpMon(Movement expenditure, Map<String, Map<String, Deal>> mapDeal) {
        Map<String, Deal> dealList = mapDeal.computeIfAbsent(expenditure.getCategory(), k -> new LinkedHashMap<>());
        Deal deal = dealList.computeIfAbsent(expenditure.getCompanyName(), k -> new Deal(expenditure.getCompanyName(), new LinkedList<>(), new LinkedList<>()));
        ExpenditureModel expenditureModel = getExpenditureModel(expenditure);
        deal.getExpenditureList().add(expenditureModel);
    }


    private void dump(Workbook wb, Sheet sheet, Map<String, Map<String, Deal>> mapDeal) {
        AtomicInteger rowNum = new AtomicInteger(6);
        mapDeal.forEach((category, dealMap) -> {
            Row rowCat = sheet.createRow(rowNum.get());
            Cell cellComp = rowCat.createCell(0);
            cellComp.setCellValue(category);
            cellComp.setCellStyle(IncomeService.getCellStyleColor(wb, ExcelHelper.getColor(ColorImp.CATEGORY), IncomeService.getFont(wb, true, null), null, VerticalAlignment.CENTER, null));
            sheet.addMergedRegion(new CellRangeAddress(rowNum.get(), rowNum.get(), 0, 17));

            rowNum.set(rowNum.get() + 2);
            ResultRow resultRow = new ResultRow(rowNum.get());
            dump(wb, sheet, resultRow, dealMap.values().stream().toList());
            createResult(wb, sheet, resultRow.getRow(), 0, "Доход по операциям с " + category, resultRow.getRevenueAmount(), IncomeService.getCellStyleColor(wb, ExcelHelper.getColor(ColorImp.RESULT), IncomeService.getFont(wb, true, (short) 9)));
            createResult(wb, sheet, resultRow.getRow() + 1, 0, "Расход по операциям с " + category, resultRow.getExpenditureAmount(), IncomeService.getCellStyleColor(wb, ExcelHelper.getColor(ColorImp.RESULT), IncomeService.getFont(wb, true, (short) 9)));
            createResult(wb, sheet, resultRow.getRow() + 2, 0, "Итого прибыль/убыток по операциям с " + category, resultRow.getRevenueAmount() - resultRow.getExpenditureAmount(), IncomeService.getCellStyleColor(wb, ExcelHelper.getColor(ColorImp.RESULT), IncomeService.getFont(wb, true, (short) 9)));

            rowNum.set(resultRow.getRow() + 4);
        });
    }
    private ExpenditureModel getExpenditureModel(Movement revenueCommission) {
        ExpenditureModel expenditureModel = new ExpenditureModel(revenueCommission.getTimestamp(), revenueCommission.getPrice(), revenueCommission.getQuantity(),  revenueCommission.getCurrencyCode(), "Основные 1530, 1535", "Комиссия за продажу",revenueCommission.getCommission());
        valuteService.updateCurrObject(expenditureModel);
        return expenditureModel;
    }

    private RevenueModel getRevenueModel(Revenue revenue) {
        RevenueModel revenueModel = new RevenueModel(revenue.getTimestamp(), revenue.getPrice(), revenue.getQuantity(), revenue.getCurrencyCode(), AssetType.Bonds.getType().toString(), "Продажа позиции");
        valuteService.updateCurrObject(revenueModel);
        return revenueModel;
    }

    private static void dump(Workbook wb, Sheet sheet, ResultRow resultRow, List<Deal> dealList) {

        for (Deal deal : dealList) {
            dump(wb, sheet, resultRow, deal);
        }

    }

    private static void dump(Workbook wb, Sheet sheet, ResultRow resultRow, Deal deal) {
        Integer rowNum = resultRow.getRow();
        Row row = sheet.createRow(rowNum);
        Cell cell = row.createCell(0);
        cell.setCellValue(deal.getCompanyCode());
        cell.setCellStyle(IncomeService.getCellStyleColor(wb, ExcelHelper.getColor(ColorImp.TABLE_HEAD), IncomeService.getFont(wb, true, (short) 9)));
        sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 0, 17));
        rowNum++;

        row = sheet.createRow(rowNum);
        cell = row.createCell(0);
        cell.setCellValue("Выручка");
        cell.setCellStyle(IncomeService.getCellStyleColor(wb, ExcelHelper.getColor(ColorImp.REVENUE), IncomeService.getFont(wb, (short) 9)));
        sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 0, 8));

//        row = sheet.createRow(rowNum);
        cell = row.createCell(9);
        cell.setCellValue("Расход");
        cell.setCellStyle(IncomeService.getCellStyleColor(wb, ExcelHelper.getColor(ColorImp.EXPENDITURE), IncomeService.getFont(wb, (short) 9)));
        sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 9, 17));
        rowNum++;
        IncomeService.createHead(sheet, rowNum, 0, new String[]{"Дата", "Цена", "Кол-во", "В валюте", "Валюта", "Курс", "В рублях", "Код", "Вид дохода"}, IncomeService.getCellStyleColor(wb, ExcelHelper.getColor(ColorImp.COLUMN_HEAD), IncomeService.getFont(wb, (short) 9)));

        IncomeService.createHead(sheet, rowNum, 9, new String[]{"Дата", "Цена", "Кол-во", "В валюте", "Валюта", "Курс", "В рублях", "Код", "Вид дохода"}, IncomeService.getCellStyleColor(wb, ExcelHelper.getColor(ColorImp.COLUMN_HEAD), IncomeService.getFont(wb, (short) 9)));
        rowNum++;
        int colRow = getColRow(deal);

        double amountRevenue = createRows(wb, sheet, rowNum, 0, deal.getRevenueList());
        double amountExpenditure = createRows(wb, sheet, rowNum, 9, deal.getExpenditureList());
        rowNum = rowNum + colRow;

        createResult(wb, sheet, rowNum, 0, "Итого доход по операции", amountRevenue, IncomeService.getCellStyleColor(wb, ExcelHelper.getColor(ColorImp.REVENUE_RESULT), IncomeService.getFont(wb, true, (short) 9)));
        createResult(wb, sheet, rowNum++, 9, "Итого расход по операции", amountExpenditure, IncomeService.getCellStyleColor(wb, ExcelHelper.getColor(ColorImp.EXPENDITURE_RESULT), IncomeService.getFont(wb, true, (short) 9)));
        rowNum = rowNum + 1;

        createResult(wb, sheet, rowNum++, 0, "Итого прибыль/убыток по позиции", amountRevenue - amountExpenditure, IncomeService.getCellStyleColor(wb, ExcelHelper.getColor(ColorImp.TABLE_RESULT), IncomeService.getFont(wb, true, (short) 9)));
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
    private static Double createRows(Workbook wb, Sheet sheet, Integer rowNum, int cellStart, List<? extends MovementModel> movementList) {

        double amountAll = 0f;
        for (MovementModel movementModel : movementList) {
            int cellStartRow = cellStart;
            Row row = sheet.getRow(rowNum);
            if (row == null) {
                row = sheet.createRow(rowNum);
            }
            IncomeService.addCell(wb, row, cellStartRow, movementModel.getTimestamp());
            cellStartRow++;

            IncomeService.addCell(wb,row, cellStartRow, movementModel.getPrice(), IncomeService.getFontStyle(wb, (short) 9));
            cellStartRow++;

            IncomeService.addCell(row, cellStartRow, movementModel.getQuantity(), IncomeService.getFontStyle(wb, (short) 9));
            cellStartRow++;

            IncomeService.addCell(wb,row, cellStartRow, movementModel.getAmountInCur(), IncomeService.getFontStyle(wb, (short) 9));
            cellStartRow++;

            IncomeService.addCell(row, cellStartRow, movementModel.getCurrencyCode() + "/" + movementModel.getCurrencyName(), IncomeService.getFontStyle(wb, (short) 9));
            cellStartRow++;

            IncomeService.addCell(wb,row, cellStartRow, movementModel.getCourse(), IncomeService.getFontStyle(wb, (short) 9));
            cellStartRow++;

            IncomeService.addCell(wb,row, cellStartRow, movementModel.getAmount(), IncomeService.getFontStyle(wb, (short) 9));
            cellStartRow++;

            IncomeService.addCell(row, cellStartRow, movementModel.getCode(), IncomeService.getFontStyle(wb, (short) 9));
            cellStartRow++;

            IncomeService.addCell(row, cellStartRow, movementModel.getTypeOf(), IncomeService.getCellStyleColor(wb,IncomeService.getFont(wb, (short) 9),true));


            amountAll = amountAll + movementModel.getAmount();
            rowNum++;
        }
        return amountAll;

    }

    private static void createResult(Workbook wb, Sheet sheet, Integer rowNum, Integer cellStart, String text, double amount, CellStyle style) {
        Row row = sheet.getRow(rowNum);
        if (row == null)
            row = sheet.createRow(rowNum);
        IncomeService.addCell(row, cellStart, text, style);
        sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, cellStart, cellStart + 5));
        IncomeService.addCell(wb, row, cellStart + 6, amount, style);
        sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, cellStart + 6, cellStart + 6 + 2));

    }


}
