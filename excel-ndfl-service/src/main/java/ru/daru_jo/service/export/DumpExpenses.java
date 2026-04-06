package ru.daru_jo.service.export;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.daru_jo.entity.OrderAccount;
import ru.daru_jo.helper.ExcelHelper;
import ru.daru_jo.model.ExpensesModel;
import ru.daru_jo.service.IncomeService;
import ru.daru_jo.service.db.*;
import ru.daru_jo.type.ColorImp;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class DumpExpenses {
    private ExpensesService expensesService;
    private ValuteService valuteService;

    @Autowired
    public void setExpensesService(ExpensesService expensesService) {
        this.expensesService = expensesService;
    }

    @Autowired
    public void setValuteService(ValuteService valuteService) {
        this.valuteService = valuteService;
    }

    private final Sort sort = Sort.by("orderAccount", "type", "date", "code");

    public double dump(Workbook wb, Sheet sheet, List<OrderAccount> orderAccountList, String year) {
        Row row = sheet.createRow(0);
        Cell cell = row.createCell(0);
        cell.setCellValue(String.format(": Раздел 4. Общие расходы за период 01/01/%s - 31/12/%s", year, year));
        cell.setCellStyle(IncomeService.getCellStyleColor(wb, ExcelHelper.getColor(ColorImp.HEAD), IncomeService.getFont(wb, IndexedColors.WHITE, true, null), HorizontalAlignment.CENTER, VerticalAlignment.CENTER, null));
        sheet.addMergedRegion(new CellRangeAddress(0, 1, 0, 12));

        AtomicInteger rowNum = new AtomicInteger(3);
        AtomicReference<Double> amount = new AtomicReference<>((double) 0);
        orderAccountList.forEach(orderAccount ->
                amount.set(amount.get() + dump(wb, sheet, rowNum, orderAccount))
        );
        return amount.get();
    }

    private double dump(Workbook wb, Sheet sheet, AtomicInteger rowNum, OrderAccount orderAccount) {
        Row row = sheet.createRow(rowNum.getAndIncrement());
        Cell cell = row.createCell(0);
        cell.setCellValue(String.format("Проценты, полученные в Interactive Brokers %s", orderAccount.getAccount()));
        cell.setCellStyle(IncomeService.getCellStyleColor(wb, ExcelHelper.getColor(ColorImp.CATEGORY), IncomeService.getFont(wb, true, null), HorizontalAlignment.CENTER, VerticalAlignment.CENTER, null));
        sheet.addMergedRegion(new CellRangeAddress(3, 3, 0, 12));
        rowNum.incrementAndGet();
        AtomicReference<Double> amount = new AtomicReference<>((double) 0);
        dump(wb, sheet, rowNum, expensesService.findAll(orderAccount, null, sort).stream().map(expenses -> {
            ExpensesModel expensesModel = new ExpensesModel(expenses);
            valuteService.updateCurrObject(expensesModel);
            amount.set(amount.get() + expensesModel.getAmount());
            return expensesModel;
        }).toList());
        return amount.get();
    }

    public void dump(Workbook wb, Sheet sheet, AtomicInteger rowNum, List<ExpensesModel> expensesModelList) {
        String code = null;

        for (ExpensesModel expensesModel : expensesModelList) {
            if (code == null) {
                rowNum.set(createTableHead(wb, sheet, rowNum.get(), expensesModel.getDescription()));
            } else if (!code.equals(expensesModel.getDescription())) {
                rowNum.set(createTableHead(wb, sheet, rowNum.incrementAndGet(), expensesModel.getDescription()));
            }
            code = expensesModel.getDescription();
            int cellStartRow = 0;
            Row row = sheet.createRow(rowNum.getAndIncrement());
            IncomeService.addCell(row, cellStartRow, expensesModel.getDescription(), IncomeService.getFontStyle(wb, (short) 9));
            cellStartRow++;

            IncomeService.addCell(wb, row, cellStartRow, expensesModel.getTimestamp());
            cellStartRow++;

            IncomeService.addCell(wb, row, cellStartRow, expensesModel.getAmountInCur(), IncomeService.getFontStyle(wb, (short) 9));
            cellStartRow++;

            IncomeService.addCell(row, cellStartRow, expensesModel.getCurrencyCode() + "/" + expensesModel.getCurrencyName(), IncomeService.getFontStyle(wb, (short) 9));
            cellStartRow++;

            IncomeService.addCell(wb, row, cellStartRow, expensesModel.getCourse(), IncomeService.getFontStyle(wb, (short) 9));
            cellStartRow++;


            IncomeService.addCell(wb, row, cellStartRow, expensesModel.getAmount(), IncomeService.getFontStyle(wb, (short) 9));
            cellStartRow++;
            IncomeService.addCell(row, cellStartRow, expensesModel.getTypeOfCode(), IncomeService.getFontStyle(wb, (short) 9));


        }
    }

    private static @NonNull Integer createTableHead(Workbook wb, Sheet sheet, Integer rowNum, String code) {
        Row row = sheet.createRow(rowNum);
        Cell cell = row.createCell(0);
        cell.setCellValue(String.format("%s", code));
        cell.setCellStyle(IncomeService.getCellStyleColor(wb, ExcelHelper.getColor(ColorImp.TABLE_HEAD), IncomeService.getFont(wb, true, (short) 9)));
        sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 0, 11));
        rowNum++;

        IncomeService.createHead(sheet, rowNum, 0, new String[]{"Наименование", "Дата", "В валюте", "Валюта", "Курс", "В рублях", "Код"}, IncomeService.getCellStyleColor(wb, ExcelHelper.getColor(ColorImp.COLUMN_HEAD), IncomeService.getFont(wb, (short) 9)));
        rowNum++;

        return rowNum;
    }
}
