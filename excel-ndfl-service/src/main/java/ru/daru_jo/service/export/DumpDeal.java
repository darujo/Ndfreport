package ru.daru_jo.service.export;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.daru_jo.entity.*;
import ru.daru_jo.helper.ExcelHelper;
import ru.daru_jo.model.*;
import ru.daru_jo.service.IncomeService;
import ru.daru_jo.service.db.ValuteService;
import ru.daru_jo.service.db.ExpenditureService;
import ru.daru_jo.service.db.RevenueService;
import ru.daru_jo.type.AssetType;
import ru.daru_jo.type.ColorImp;
import ru.daru_jo.type.DealType;
import ru.daru_jo.type.OperationType;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class DumpDeal {
    private RevenueService revenueService;
    private ExpenditureService expenditureService;
    private ValuteService valuteService;
    private DumpCoupon dumpCoupon;

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

    @Autowired
    public void setDumpCoupon(DumpCoupon dumpCoupon) {
        this.dumpCoupon = dumpCoupon;
    }

    public void dump(Workbook wb, Sheet sheet, Order order) {
        Map<String, Map<String, Deal>> mapDeal = addMove(order);
        dumpCoupon.addCoupon(order,mapDeal);
        Row row = sheet.createRow(0);
        Cell cell = row.createCell(0);
        cell.setCellValue(String.format("Раздел 1. Доходы от операций с ценными бумагами и иными финансовыми инструментами за период 01/01/%s - 31/12/%s ", order.getYear(), order.getYear()));
        cell.setCellStyle(IncomeService.getCellStyleColor(wb, ExcelHelper.getColor(ColorImp.HEAD), IncomeService.getFont(wb, IndexedColors.WHITE, true, null), HorizontalAlignment.CENTER, VerticalAlignment.CENTER, null));
        sheet.addMergedRegion(new CellRangeAddress(0, 2, 0, 17));
        row = sheet.createRow(3);
        cell = row.createCell(0);
        cell.setCellValue("Обращаем внимание на то, что при отражении сумм комиссии на покупку и продажу ЦБ и ПФИ справочно напротив сумм комиссии в расчетной таблице указано количество. Показатель \"количество\" для комиссии является справочным, т.к. сумма комиссии по покупке указана с учетом пропорционального распределения от общей суммы комиссии на покупку.");
        cell.setCellStyle(IncomeService.getCellStyleColor(wb, null, IncomeService.getFont(wb, (short) 9), HorizontalAlignment.LEFT, VerticalAlignment.CENTER, true));
        sheet.addMergedRegion(new CellRangeAddress(3, 5, 0, 17));

        dump(wb, sheet, mapDeal, order.getAccount());
    }
    private Map<String, Map<String, Deal>> addMove(Order order){
        List<Revenue> revenueList = revenueService.findAll(order);
        Map<String, Map<String, Deal>> mapDeal = new LinkedHashMap<>();
        revenueList.forEach(revenue -> {
            Map<String, Deal> dealList = mapDeal.computeIfAbsent(revenue.getCategory(), k -> new LinkedHashMap<>());
            Deal deal = dealList.computeIfAbsent(revenue.getCompanyName(), k -> new Deal(revenue.getCompanyName(), new LinkedList<>()));
            RevenueModel revenueModel = getRevenueModel(revenue, OperationType.SALE);
            deal.getRevenueList().add(revenueModel);
            addMove(revenue, revenueModel, OperationType.COMMISSION_SALE);

        });
        expenditureService.findAll(order).forEach(expenditure -> addMoveAndMinus(expenditure, mapDeal));
        return mapDeal;
    }
    private void addMoveAndMinus(Movement movement, Map<String, Map<String, Deal>> mapDeal) {
        ExpenditureModel expenditureModel = getExpenditureModel(movement, OperationType.BUY);
        if (expenditureModel != null) {
            Map<String, Deal> dealList = mapDeal.computeIfAbsent(movement.getCategory(), k -> new LinkedHashMap<>());
            Deal deal = dealList.computeIfAbsent(movement.getCompanyName(), k -> new Deal(movement.getCompanyName(), new LinkedList<>()));
            for (RevenueModel revenueModel : deal.getRevenueList()) {
                if (revenueModel.getQuantityDistributed() != null && revenueModel.getQuantityDistributed() > 0 && revenueModel.getTimestamp().before(expenditureModel.getTimestamp())) {
                    Double quantityDistributed = expenditureModel.getQuantityDistributed();
                    revenueModel.addExpenditureAndMinus(expenditureModel);
                    if (quantityDistributed > expenditureModel.getQuantityDistributed()) {
                        ExpenditureModel commissionModel = getExpenditureModel(movement, OperationType.COMMISSION_BUY, (quantityDistributed - expenditureModel.getQuantityDistributed()) / expenditureModel.getQuantity());
                        if(commissionModel != null) {
                            revenueModel.addExpenditure(commissionModel);
                        }
                    }
                }
                if (expenditureModel.getQuantityDistributed().equals(0d)) {
                    break;
                }
            }
            if (expenditureModel.getQuantityDistributed() > 0) {
                deal.addExpenditureModelNotRevenue(expenditureModel);
            }
        }
    }

    private void addMove(Movement movement, RevenueModel revenueModel, OperationType operationType) {
        ExpenditureModel expenditureModel = getExpenditureModel(movement, operationType);
        if (expenditureModel != null) {

            revenueModel.addExpenditure(expenditureModel);
        }
    }

    private void dump(Workbook wb, Sheet sheet, Map<String, Map<String, Deal>> mapDeal, String account) {
        AtomicInteger rowNum = new AtomicInteger(6);
        mapDeal.forEach((category, dealMap) -> {
            Row rowCat = sheet.createRow(rowNum.get());
            Cell cellComp = rowCat.createCell(0);
            String categoryName = DealType.valueOf(category).getName().replace("#acct#", account);
            cellComp.setCellValue(categoryName);
            cellComp.setCellStyle(IncomeService.getCellStyleColor(wb, ExcelHelper.getColor(ColorImp.CATEGORY), IncomeService.getFont(wb, true, null), null, VerticalAlignment.CENTER, null));
            sheet.addMergedRegion(new CellRangeAddress(rowNum.get(), rowNum.get(), 0, 17));

            rowNum.set(rowNum.get() + 2);
            ResultRow resultRow = new ResultRow(rowNum.get());
            dealMap.values().forEach(deal -> {
                deal.getRevenueList().forEach(revenueModel -> {

                    valuteService.updateCurrObject(revenueModel);
                    revenueModel.getExpenditureModelList().forEach(expenditureModel ->

                            valuteService.updateCurrObject(expenditureModel));

                });
                deal.getExpenditureModelNotRevenueList().forEach(expenditureModel -> valuteService.updateCurrObject(expenditureModel));
            });
            dump(wb, sheet, resultRow, dealMap.values().stream().toList());
            createResult(wb, sheet, resultRow.getRow(), 0, "Доход по операциям с " + categoryName, resultRow.getRevenueAmount(), IncomeService.getCellStyleColor(wb, ExcelHelper.getColor(ColorImp.RESULT), IncomeService.getFont(wb, true, (short) 9)));
            createResult(wb, sheet, resultRow.getRow() + 1, 0, "Расход по операциям с " + categoryName, resultRow.getExpenditureAmount(), IncomeService.getCellStyleColor(wb, ExcelHelper.getColor(ColorImp.RESULT), IncomeService.getFont(wb, true, (short) 9)));
            createResult(wb, sheet, resultRow.getRow() + 2, 0, "Итого прибыль/убыток по операциям с " + categoryName, resultRow.getRevenueAmount() - resultRow.getExpenditureAmount(), IncomeService.getCellStyleColor(wb, ExcelHelper.getColor(ColorImp.RESULT), IncomeService.getFont(wb, true, (short) 9)));

            rowNum.set(resultRow.getRow() + 4);
        });
    }

    private ExpenditureModel getExpenditureModel(Movement movement, OperationType operationType) {
        return getExpenditureModel(movement, operationType, 1d);
    }

    private ExpenditureModel getExpenditureModel(Movement movement, OperationType operationType, Double ratio) {
        ExpenditureModel expenditureModel = null;
        if (operationType.equals(OperationType.BUY)) {
            expenditureModel = new ExpenditureModel(operationType, movement.getTimestamp(), movement.getPrice(), movement.getQuantity(), movement.getCurrencyCode(), Integer.toString(AssetType.valueOf(movement.getType()).getBuy()), operationType.getText(), null);
        } else if (operationType.equals(OperationType.COMMISSION_SALE) && movement.getCommission() != null) {
            expenditureModel = new ExpenditureModel(operationType, movement.getTimestamp(), null, movement.getQuantity(), movement.getCurrencyCode(), Integer.toString(AssetType.valueOf(movement.getType()).getCommissionSale()), operationType.getText(), movement.getCommission() * ratio);
        } else if (operationType.equals(OperationType.COMMISSION_BUY) && movement.getCommission() != null) {
            expenditureModel = new ExpenditureModel(operationType, movement.getTimestamp(), null, movement.getQuantity(), movement.getCurrencyCode(), Integer.toString(AssetType.valueOf(movement.getType()).getCommissionBuy()), operationType.getText(), movement.getCommission() * ratio);
        }
//        if (expenditureModel != null) {
//            valuteService.updateCurrObject(expenditureModel);
//
//        }
        return expenditureModel;
    }

    private RevenueModel getRevenueModel(Revenue revenue, OperationType operationType) {
        RevenueModel revenueModel = new RevenueModel(operationType, revenue.getTimestamp(), revenue.getPrice(), revenue.getQuantity(), revenue.getCurrencyCode(), Integer.toString(AssetType.valueOf(revenue.getType()).getRevenueCode()), operationType.getText());
        valuteService.updateCurrObject(revenueModel);
        return revenueModel;
    }

    private static void dump(Workbook wb, Sheet sheet, ResultRow resultRow, List<Deal> dealList) {

        for (Deal deal : dealList) {
            dump(wb, sheet, resultRow, deal);
        }

    }

    private static void dump(Workbook wb, Sheet sheet, ResultRow resultRow, Deal deal) {
        AtomicInteger rowNum = new AtomicInteger(resultRow.getRow());
        Row row = sheet.createRow(rowNum.get());
        Cell cell = row.createCell(0);
        cell.setCellValue(deal.getCompanyCode());
        cell.setCellStyle(IncomeService.getCellStyleColor(wb, ExcelHelper.getColor(ColorImp.TABLE_HEAD), IncomeService.getFont(wb, true, (short) 9)));
        sheet.addMergedRegion(new CellRangeAddress(rowNum.get(), rowNum.get(), 0, 17));


        row = sheet.createRow(rowNum.incrementAndGet());
        cell = row.createCell(0);
        cell.setCellValue("Доход");
        cell.setCellStyle(IncomeService.getCellStyleColor(wb, ExcelHelper.getColor(ColorImp.REVENUE), IncomeService.getFont(wb, (short) 9)));
        sheet.addMergedRegion(new CellRangeAddress(rowNum.get(), rowNum.get(), 0, 8));

//        row = sheet.createRow(rowNum);
        cell = row.createCell(9);
        cell.setCellValue("Расход");
        cell.setCellStyle(IncomeService.getCellStyleColor(wb, ExcelHelper.getColor(ColorImp.EXPENDITURE), IncomeService.getFont(wb, (short) 9)));
        sheet.addMergedRegion(new CellRangeAddress(rowNum.get(), rowNum.get(), 9, 17));

        deal.getRevenueList().forEach(revenueModel -> {
            IncomeService.createHead(sheet, rowNum.incrementAndGet(), 0, new String[]{"Дата", "Цена", "Кол-во", "В валюте", "Валюта", "Курс", "В рублях", "Код", "Вид дохода"}, IncomeService.getCellStyleColor(wb, ExcelHelper.getColor(ColorImp.COLUMN_HEAD), IncomeService.getFont(wb, (short) 9)));

            IncomeService.createHead(sheet, rowNum.get(), 9, new String[]{"Дата", "Цена", "Кол-во", "В валюте", "Валюта", "Курс", "В рублях", "Код", "Вид дохода"}, IncomeService.getCellStyleColor(wb, ExcelHelper.getColor(ColorImp.COLUMN_HEAD), IncomeService.getFont(wb, (short) 9)));

            int colRow = getColRow(revenueModel);

            double amountRevenue = createRows(wb, sheet, rowNum.incrementAndGet(), 0, revenueModel);
            double amountExpenditure = createRows(wb, sheet, rowNum.get(), 9, revenueModel.getExpenditureModelList());
            rowNum.set(rowNum.get() + colRow);

            createResult(wb, sheet, rowNum.get(), 0, "Итого доход по операции", amountRevenue, IncomeService.getCellStyleColor(wb, ExcelHelper.getColor(ColorImp.REVENUE_RESULT), IncomeService.getFont(wb, true, (short) 9)));
            createResult(wb, sheet, rowNum.getAndIncrement(), 9, "Итого расход по операции", amountExpenditure, IncomeService.getCellStyleColor(wb, ExcelHelper.getColor(ColorImp.EXPENDITURE_RESULT), IncomeService.getFont(wb, true, (short) 9)));
            rowNum.incrementAndGet();

            createResult(wb, sheet, rowNum.getAndIncrement(), 0, "Итого прибыль/убыток по позиции", amountRevenue - amountExpenditure, IncomeService.getCellStyleColor(wb, ExcelHelper.getColor(ColorImp.TABLE_RESULT), IncomeService.getFont(wb, true, (short) 9)));

            resultRow.setRevenueAmount(resultRow.getRevenueAmount() + amountRevenue);
            resultRow.setExpenditureAmount(resultRow.getExpenditureAmount() + amountExpenditure);
        });
        resultRow.setRow(rowNum.incrementAndGet() );
    }

    private static int getColRow(RevenueModel revenueModel) {
        int colRow;
        if (revenueModel.getExpenditureModelList() == null) {
            colRow = 1;
        } else {
            colRow = revenueModel.getExpenditureModelList().size();
        }
        return colRow;
    }

    private static Double createRows(Workbook wb, Sheet sheet, Integer rowNum, int cellStart, MovementModel movementModel) {
        int cellStartRow = cellStart;
        Row row = sheet.getRow(rowNum);
        if (row == null) {
            row = sheet.createRow(rowNum);
        }
        IncomeService.addCell(wb, row, cellStartRow, movementModel.getTimestamp());
        cellStartRow++;

        IncomeService.addCell(wb, row, cellStartRow, movementModel.getPrice(), IncomeService.getFontStyle(wb, (short) 9));
        cellStartRow++;

        IncomeService.addCell(row, cellStartRow, movementModel.getQuantity(), IncomeService.getFontStyle(wb, (short) 9));
        cellStartRow++;

        IncomeService.addCell(wb, row, cellStartRow, movementModel.getAmountInCur(), IncomeService.getFontStyle(wb, (short) 9));
        cellStartRow++;

        IncomeService.addCell(row, cellStartRow, movementModel.getCurrencyCode() + "/" + movementModel.getCurrencyName(), IncomeService.getFontStyle(wb, (short) 9));
        cellStartRow++;

        IncomeService.addCell(wb, row, cellStartRow, movementModel.getCourse(), IncomeService.getFontStyle(wb, (short) 9));
        cellStartRow++;

        IncomeService.addCell(wb, row, cellStartRow, movementModel.getAmount(), IncomeService.getFontStyle(wb, (short) 9));
        cellStartRow++;

        IncomeService.addCell(row, cellStartRow, movementModel.getCode(), IncomeService.getFontStyle(wb, (short) 9));
        cellStartRow++;

        IncomeService.addCell(row, cellStartRow, movementModel.getTypeOf(), IncomeService.getCellStyleColor(wb, IncomeService.getFont(wb, (short) 9), true));
        return movementModel.getAmount();
    }

    private static Double createRows(Workbook wb, Sheet sheet, Integer rowNum, int cellStart, List<? extends MovementModel> movementList) {

        double amountAll = 0f;
        for (MovementModel movementModel : movementList) {

            amountAll = amountAll + createRows(wb, sheet, rowNum++, cellStart, movementModel);

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
