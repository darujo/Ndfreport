package ru.daru_jo.service.export;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.daru_jo.entity.Dividend;
import ru.daru_jo.entity.Order;
import ru.daru_jo.helper.ExcelHelper;
import ru.daru_jo.model.DividendModel;
import ru.daru_jo.model.DividendRevenueModel;
import ru.daru_jo.model.DividendTaxModel;
import ru.daru_jo.model.ResultRow;
import ru.daru_jo.service.IncomeService;
import ru.daru_jo.service.db.DividendService;
import ru.daru_jo.service.db.DividendTaxService;
import ru.daru_jo.service.db.ValuteService;
import ru.daru_jo.type.ColorImp;

import java.sql.Timestamp;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Service
public class DumpDividend {
    private DividendService dividendService;
    private DividendTaxService dividendTaxService;
    private ValuteService valuteService;

    @Autowired
    public void setDividendService(DividendService dividendService) {
        this.dividendService = dividendService;
    }

    @Autowired
    public void setDividendTaxService(DividendTaxService dividendTaxService) {
        this.dividendTaxService = dividendTaxService;
    }

    @Autowired
    public void setValuteService(ValuteService valuteService) {
        this.valuteService = valuteService;
    }

    public void dump(Workbook wb, Sheet sheet, Order order) {
        Row row = sheet.createRow(0);
        Cell cell = row.createCell(0);
        cell.setCellValue(String.format("Раздел 2. Доходы по дивидендам за период 01/01/%s - 31/12/%s", order.getYear(), order.getYear()));
        cell.setCellStyle(IncomeService.getCellStyleColor(wb, ExcelHelper.getColor(ColorImp.HEAD), IncomeService.getFont(wb, IndexedColors.WHITE, true, null), HorizontalAlignment.CENTER, VerticalAlignment.CENTER, null));
        sheet.addMergedRegion(new CellRangeAddress(0, 1, 0, 11));
        Map<String, Map<Timestamp, Map<String, DividendModel>>> currencyDateCodeDividendMap = addDividend(order);
        AtomicInteger rowNum = new AtomicInteger(1);
        currencyDateCodeDividendMap.keySet().forEach(currency -> dumpCurrency(wb, sheet, rowNum, order, currency, currencyDateCodeDividendMap.get(currency)));
    }

    private void dumpCurrency(Workbook wb, Sheet sheet, AtomicInteger rowNum, Order order, String currency, Map<Timestamp, Map<String, DividendModel>> dateCodeDividendModelMap) {
        rowNum.incrementAndGet();
        Row row = sheet.createRow(rowNum.incrementAndGet());
        Cell cell = row.createCell(0);
        cell.setCellValue(String.format("Дивиденды, полученные в Interactive Brokers %s в валюте %s", order.getAccount(), currency));
        cell.setCellStyle(IncomeService.getCellStyleColor(wb, ExcelHelper.getColor(ColorImp.CATEGORY), IncomeService.getFont(wb, true, null), HorizontalAlignment.CENTER, VerticalAlignment.CENTER, null));
        sheet.addMergedRegion(new CellRangeAddress(rowNum.get(), rowNum.get(), 0, 13));
        AtomicReference<Double> amount = new AtomicReference<>(0d);
        AtomicReference<Double> amountTax = new AtomicReference<>(0d);
        dateCodeDividendModelMap.keySet().forEach(timestamp -> {
            ResultRow resultRow = new ResultRow(rowNum.incrementAndGet());
            dumpDate(wb, sheet, rowNum, currency, dateCodeDividendModelMap.get(timestamp), resultRow);
            amount.set(amount.get() + resultRow.getRevenueAmount());
            amountTax.set(amountTax.get() + resultRow.getExpenditureAmount());
        });
        row = sheet.createRow(rowNum.incrementAndGet());
        cell = row.createCell(0);
        cell.setCellValue(" Итого прибыль:");
        cell.setCellStyle(IncomeService.getCellStyleColor(wb, ExcelHelper.getColor(ColorImp.RESULT), IncomeService.getFont(wb, true, (short) 9)));
        sheet.addMergedRegion(new CellRangeAddress(rowNum.get(), rowNum.get(), 0, 2));

        cell = row.createCell(3);
        cell.setCellValue(amount.get());
        cell.setCellStyle(IncomeService.getCellStyleColor(wb, ExcelHelper.getColor(ColorImp.RESULT), IncomeService.getFont(wb, true, (short) 9)));
        sheet.addMergedRegion(new CellRangeAddress(rowNum.get(), rowNum.get(), 3, 5));
        cell = row.createCell(6);
        cell.setCellValue(amountTax.get());
        cell.setCellStyle(IncomeService.getCellStyleColor(wb, ExcelHelper.getColor(ColorImp.RESULT), IncomeService.getFont(wb, true, (short) 9)));
        sheet.addMergedRegion(new CellRangeAddress(rowNum.get(), rowNum.get(), 6, 12));

        cell = row.createCell(13);
        cell.setCellValue(amount.get() * 0.13 - amountTax.get());
        cell.setCellStyle(IncomeService.getCellStyleColor(wb, ExcelHelper.getColor(ColorImp.RESULT), IncomeService.getFont(wb, true, (short) 9)));
    }

    private void dumpDate(Workbook wb, Sheet sheet, AtomicInteger rowNum, String currency, Map<String, DividendModel> codeDividendModelMap, ResultRow resultRow) {
        codeDividendModelMap.forEach((code, dividendModel) ->
                dumpCode(wb, sheet, rowNum, code, currency, dividendModel, resultRow)
        );
    }

    private void dumpCode(Workbook wb, Sheet sheet, AtomicInteger rowNum, String code, String currency, DividendModel dividendModel, ResultRow resultRow) {
        Row row = sheet.createRow(rowNum.incrementAndGet());
        Cell cell = row.createCell(0);
        cell.setCellValue(code + " (код страны " + currency + ")");
        cell.setCellStyle(IncomeService.getCellStyleColor(wb, ExcelHelper.getColor(ColorImp.TABLE_HEAD), IncomeService.getFont(wb, true, (short) 9)));
        sheet.addMergedRegion(new CellRangeAddress(rowNum.get(), rowNum.get(), 0, 13));

        row = sheet.createRow(rowNum.incrementAndGet());
        cell = row.createCell(0);
        cell.setCellValue("Доход");
        cell.setCellStyle(IncomeService.getCellStyleColor(wb, ExcelHelper.getColor(ColorImp.REVENUE), IncomeService.getFont(wb, (short) 9)));
        sheet.addMergedRegion(new CellRangeAddress(rowNum.get(), rowNum.get(), 0, 6));
        cell = row.createCell(7);
        cell.setCellValue("Налог удержан");
        cell.setCellStyle(IncomeService.getCellStyleColor(wb, ExcelHelper.getColor(ColorImp.EXPENDITURE), IncomeService.getFont(wb, (short) 9)));
        sheet.addMergedRegion(new CellRangeAddress(rowNum.get(), rowNum.get(), 7, 13));

        IncomeService.createHead(sheet, rowNum.incrementAndGet(), 0, new String[]{"Дата", "В валюте", "Валюта", "Курс", "Код", "В рублях", "Страна"}, IncomeService.getCellStyleColor(wb, ExcelHelper.getColor(ColorImp.COLUMN_HEAD), IncomeService.getFont(wb, (short) 9)));
        IncomeService.createHead(sheet, rowNum.get(), 7, new String[]{"Ставка", "В валюте", "Дата", "Курс", "В рублях", "Подлежит зачету", "К уплате в РФ"}, IncomeService.getCellStyleColor(wb, ExcelHelper.getColor(ColorImp.COLUMN_HEAD), IncomeService.getFont(wb, (short) 9)));
        Double amount = dumpDividendRevenue(wb, sheet, rowNum.incrementAndGet(), dividendModel.getDividendRevenueModel());
        Double amountTax = dumpDividendTax(wb, sheet, rowNum.get(), dividendModel);
        resultRow.setRevenueAmount(amount);
        resultRow.setExpenditureAmount(amountTax);
    }


    private Double dumpDividendRevenue(Workbook wb, Sheet sheet, Integer rowNum, DividendRevenueModel dividendModel) {
        int cellStartRow = 0;
        Row row = sheet.getRow(rowNum);
        if (row == null) {
            row = sheet.createRow(rowNum);
        }
        IncomeService.addCell(wb, row, cellStartRow, dividendModel.getTimestamp());
        cellStartRow++;

        IncomeService.addCell(wb, row, cellStartRow, dividendModel.getAmountInCur(), IncomeService.getFontStyle(wb, (short) 9));
        cellStartRow++;

        IncomeService.addCell(row, cellStartRow, dividendModel.getCurrencyCode() + "/" + dividendModel.getCurrencyName(), IncomeService.getFontStyle(wb, (short) 9));
        cellStartRow++;

        IncomeService.addCell(wb, row, cellStartRow, dividendModel.getCourse(), IncomeService.getFontStyle(wb, (short) 9));
        cellStartRow++;

        IncomeService.addCell(row, cellStartRow, dividendModel.getTypeOf(), IncomeService.getFontStyle(wb, (short) 9));
        cellStartRow++;

        IncomeService.addCell(wb, row, cellStartRow, dividendModel.getAmount(), IncomeService.getFontStyle(wb, (short) 9));
        cellStartRow++;


        IncomeService.addCell(row, cellStartRow, dividendModel.getCountry(), IncomeService.getFontStyle(wb, (short) 9));

        return dividendModel.getAmount();
    }

    private Double dumpDividendTax(Workbook wb, Sheet sheet, Integer rowNum, DividendModel dividendModel) {
        int cellStartRow = 7;
        Row row = sheet.getRow(rowNum);
        if (row == null) {
            row = sheet.createRow(rowNum);
        }
        IncomeService.addCellPercent(wb, row, cellStartRow, dividendModel.getDividendTaxModel().getAmount() / dividendModel.getDividendRevenueModel().getAmount()  , IncomeService.getFontStyle(wb, (short) 9));
        cellStartRow++;

        IncomeService.addCell(wb, row, cellStartRow, dividendModel.getDividendTaxModel().getAmountInCur(), IncomeService.getFontStyle(wb, (short) 9));
        cellStartRow++;

        IncomeService.addCell(wb, row, cellStartRow, dividendModel.getDividendTaxModel().getTimestamp());
        cellStartRow++;


        IncomeService.addCell(wb, row, cellStartRow, dividendModel.getDividendTaxModel().getCourse(), IncomeService.getFontStyle(wb, (short) 9));
        cellStartRow++;

        IncomeService.addCell(wb, row, cellStartRow, dividendModel.getDividendTaxModel().getAmount(), IncomeService.getFontStyle(wb, (short) 9));
        cellStartRow++;

        double taxPay = dividendModel.getDividendRevenueModel().getAmount() * 0.1 >= dividendModel.getDividendTaxModel().getAmount()
                ?
                dividendModel.getDividendTaxModel().getAmount()
                :
                dividendModel.getDividendRevenueModel().getAmount() * 0.1;

        IncomeService.addCell(wb, row, cellStartRow, taxPay, IncomeService.getFontStyle(wb, (short) 9));
        Double tax = dividendModel.getDividendRevenueModel().getAmount() * 0.13 - taxPay;
        cellStartRow++;
        IncomeService.addCell(wb, row, cellStartRow, tax, IncomeService.getFontStyle(wb, (short) 9));

        return taxPay;
    }

    private Map<String, Map<Timestamp, Map<String, DividendModel>>> addDividend(Order order) {
        Map<String, Map<Timestamp, Map<String, DividendModel>>> currencyCodeDividendMap = new LinkedHashMap<>();
        dividendService.findAll(order, Sort.by("order", "date", "code")).forEach(dividend -> {
            Map<String, DividendModel> codeDividendMap = currencyCodeDividendMap
                    .computeIfAbsent(dividend.getCurrency(), s -> new LinkedHashMap<>())
                    .computeIfAbsent(dividend.getDate(), timestamp -> new LinkedHashMap<>());
            DividendModel div = codeDividendMap.get(dividend.getCode());
            if (div == null) {
                codeDividendMap.put(dividend.getCode(),
                        new DividendModel(
                                dividend.getCurrency(),
                                dividend.getCode(),
                                getDividendRevenueModel(dividend),
                                getDividendTaxModel(order, dividend))
                );
            } else {
                if (div.getCode().equals(dividend.getCode()) && dividend.getDate().equals(div.getDividendRevenueModel().getTimestamp())) {
                    div.getDividendRevenueModel().addAmountInCur(dividend.getAmount());
                } else {
                    log.error("тикет {} в две даты {} и {}", dividend.getCode(), dividend.getDate(), div.getDividendRevenueModel().getTimestamp());
                }
            }
        });
        return currencyCodeDividendMap;
    }

    private DividendRevenueModel getDividendRevenueModel(Dividend dividend) {
        DividendRevenueModel dividendRevenueModel = new DividendRevenueModel(
                dividend.getCode(),
                dividend.getDate(),
                dividend.getAmount(),
                dividend.getCurrency(),
                dividend.getCountry(),
                "1010");
        valuteService.updateCurrObject(dividendRevenueModel);
        return dividendRevenueModel;
    }

    private DividendTaxModel getDividendTaxModel(Order order, Dividend dividend) {
        AtomicReference<Double> amount = new AtomicReference<>(0d);
        dividendTaxService
                .findAll(order, dividend.getCode(), dividend.getDate(), Sort.by("order", "code", "date"))
                .forEach(dividendTax -> amount.set(amount.get() + dividendTax.getAmount()));
        amount.set(amount.get() * -1);
        DividendTaxModel dividendRevenueModel = new DividendTaxModel(
                dividend.getCode(),
                dividend.getDate(),
                amount.get(),
                dividend.getCurrency());
        valuteService.updateCurrObject(dividendRevenueModel);
        return dividendRevenueModel;
    }

}
