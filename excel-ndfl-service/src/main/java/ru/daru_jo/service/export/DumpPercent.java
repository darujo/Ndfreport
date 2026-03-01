package ru.daru_jo.service.export;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.daru_jo.entity.Order;
import ru.daru_jo.entity.OrderAccount;
import ru.daru_jo.helper.ExcelHelper;
import ru.daru_jo.model.PercentModel;
import ru.daru_jo.service.IncomeService;
import ru.daru_jo.service.db.OrderAccountService;
import ru.daru_jo.service.db.PercentService;
import ru.daru_jo.service.db.ValuteService;
import ru.daru_jo.type.AssetType;
import ru.daru_jo.type.ColorImp;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class DumpPercent {
    private PercentService percentService;
    private ValuteService valuteService;
    private OrderAccountService orderAccountService;

    @Autowired
    public void setPercentService(PercentService percentService) {
        this.percentService = percentService;
    }

    @Autowired
    public void setValuteService(ValuteService valuteService) {
        this.valuteService = valuteService;
    }

    private final Sort sort = Sort.by("orderAccount", "type", "date", "code");

    public void dump(Workbook wb, Sheet sheet, Order order, String year) {
        Row row = sheet.createRow(0);
        Cell cell = row.createCell(0);
        cell.setCellValue(String.format("Раздел 3. Доходы по процентам за период  01/01/%s - 31/12/%s", year, year));
        cell.setCellStyle(IncomeService.getCellStyleColor(wb, ExcelHelper.getColor(ColorImp.HEAD), IncomeService.getFont(wb, IndexedColors.WHITE, true, null), HorizontalAlignment.CENTER, VerticalAlignment.CENTER, null));
        sheet.addMergedRegion(new CellRangeAddress(0, 1, 0, 12));

        AtomicInteger rowNum = new AtomicInteger(3);
        orderAccountService.findAll(order, year).forEach(orderAccount ->
                dump(wb, sheet, rowNum, orderAccount)
        );
    }

    private void dump(Workbook wb, Sheet sheet, AtomicInteger rowNum, OrderAccount orderAccount) {
        Row row = sheet.createRow(rowNum.getAndIncrement());
        Cell cell = row.createCell(0);
        cell.setCellValue(String.format("Проценты, полученные в Interactive Brokers %s", orderAccount.getAccount()));
        cell.setCellStyle(IncomeService.getCellStyleColor(wb, ExcelHelper.getColor(ColorImp.CATEGORY), IncomeService.getFont(wb, true, null), HorizontalAlignment.CENTER, VerticalAlignment.CENTER, null));
        sheet.addMergedRegion(new CellRangeAddress(3, 3, 0, 12));
        rowNum.incrementAndGet();
        dump(wb, sheet, rowNum, percentService.findAll(orderAccount, null, sort).stream().map(percent -> {
            PercentModel percentModel = new PercentModel(percent);
            valuteService.updateCurrObject(percentModel);
            return percentModel;
        }).toList());
    }

    public void dump(Workbook wb, Sheet sheet, AtomicInteger rowNum, List<PercentModel> percentModelList) {
        String code = null;

        for (PercentModel percentModel : percentModelList) {
            if (code == null) {
                rowNum.set(createTableHead(wb, sheet, rowNum.get(), percentModel.getCode(), percentModel.getCurrencyName()));


            } else if (!code.equals(percentModel.getCode())) {

                rowNum.set(createTableHead(wb, sheet, rowNum.incrementAndGet(), percentModel.getCode(), percentModel.getCurrencyName()));
            }
            code = percentModel.getCode();
            int cellStartRow = 0;
            Row row = sheet.createRow(rowNum.getAndIncrement());
            IncomeService.addCell(wb, row, cellStartRow, percentModel.getTimestamp());
            cellStartRow++;

            IncomeService.addCell(wb, row, cellStartRow, percentModel.getAmount(), IncomeService.getFontStyle(wb, (short) 9));
            cellStartRow++;

            IncomeService.addCell(row, cellStartRow, percentModel.getCurrencyCode() + "/" + percentModel.getCurrencyName(), IncomeService.getFontStyle(wb, (short) 9));
            cellStartRow++;

            IncomeService.addCell(wb, row, cellStartRow, percentModel.getCourse(), IncomeService.getFontStyle(wb, (short) 9));
            cellStartRow++;

            IncomeService.addCell(row, cellStartRow, AssetType.Bonds.getRevenueCode(), IncomeService.getFontStyle(wb, (short) 9));
            cellStartRow++;

            IncomeService.addCell(wb, row, cellStartRow, percentModel.getAmount(), IncomeService.getFontStyle(wb, (short) 9));
            cellStartRow++;
            IncomeService.addCell(row, cellStartRow, ValuteService.getCountry(percentModel.getCurrencyCode()), IncomeService.getFontStyle(wb, (short) 9));


        }
    }

    private static @NonNull Integer createTableHead(Workbook wb, Sheet sheet, Integer rowNum, String code, String currency) {
        Row row = sheet.createRow(rowNum);
        Cell cell = row.createCell(0);
        cell.setCellValue(String.format("%s (код страны %s)", code, currency));
        cell.setCellStyle(IncomeService.getCellStyleColor(wb, ExcelHelper.getColor(ColorImp.TABLE_HEAD), IncomeService.getFont(wb, true, (short) 9)));
        sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 0, 11));
        rowNum++;

        row = sheet.createRow(rowNum);
        cell = row.createCell(0);
        cell.setCellValue("Выручка");
        cell.setCellStyle(IncomeService.getCellStyleColor(wb, ExcelHelper.getColor(ColorImp.REVENUE), IncomeService.getFont(wb, (short) 9)));
        sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 0, 6));

        cell = row.createCell(7);
        cell.setCellValue("Налог удержан");
        cell.setCellStyle(IncomeService.getCellStyleColor(wb, ExcelHelper.getColor(ColorImp.EXPENDITURE), IncomeService.getFont(wb, (short) 9)));
        sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 7, 11));
        rowNum++;

        IncomeService.createHead(sheet, rowNum, 0, new String[]{"Дата", "В валюте", "Валюта", "Курс", "Код", "В рублях", "Страна"}, IncomeService.getCellStyleColor(wb, ExcelHelper.getColor(ColorImp.COLUMN_HEAD), IncomeService.getFont(wb, (short) 9)));
        IncomeService.createHead(sheet, rowNum, 7, new String[]{"Ставка", "В валюте", "Дата", "Курс", "В рублях"}, IncomeService.getCellStyleColor(wb, ExcelHelper.getColor(ColorImp.COLUMN_HEAD), IncomeService.getFont(wb, (short) 9)));
        rowNum++;

        return rowNum;
    }

    @Autowired
    public void setOrderAccountService(OrderAccountService orderAccountService) {
        this.orderAccountService = orderAccountService;
    }
}
