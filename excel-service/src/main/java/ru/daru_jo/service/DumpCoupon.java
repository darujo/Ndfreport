package ru.daru_jo.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.daru_jo.entity.Order;
import ru.daru_jo.helper.ExcelHelper;
import ru.daru_jo.model.AssetType;
import ru.daru_jo.model.CouponModel;

import java.util.List;

@Service
public class DumpCoupon {
    private CouponService couponService;
    private ValuteService valuteService;

    @Autowired
    public void setCouponService(CouponService couponService) {
        this.couponService = couponService;
    }

    @Autowired
    public void setValuteService(ValuteService valuteService) {
        this.valuteService = valuteService;
    }

    public void dump(Workbook wb, Sheet sheet, Order order) {
        Row row = sheet.createRow(0);
        Cell cell = row.createCell(0);
        cell.setCellValue(String.format("Раздел 1.2. Доходы по купонам за период 01/01/%s - 31/12/%s", order.getYear(), order.getYear()));
        cell.setCellStyle(IncomeService.getCellStyleColor(wb, ExcelHelper.getColor(ColorImp.HEAD), IncomeService.getFont(wb, IndexedColors.WHITE, true, null), HorizontalAlignment.CENTER, VerticalAlignment.CENTER, null));
        sheet.addMergedRegion(new CellRangeAddress(0, 1, 0, 12));

        cell = row.createCell(3);
        cell.setCellValue(String.format("Купоны, полученные в Interactive Brokers %s", order.getAccount()));
        cell.setCellStyle(IncomeService.getCellStyleColor(wb, ExcelHelper.getColor(ColorImp.CATEGORY), IncomeService.getFont(wb, true, null), HorizontalAlignment.CENTER, VerticalAlignment.CENTER, null));
        sheet.addMergedRegion(new CellRangeAddress(3, 3, 0, 12));

        dump(wb, sheet, couponService.findAll(order, Sort.by("order", "code", "date")).stream().map(coupon -> {
            CouponModel couponModel = new CouponModel(coupon);
            valuteService.updateCurrObject(couponModel);
            return couponModel;
        }).toList());
    }

    public void dump(Workbook wb, Sheet sheet, List<CouponModel> couponList) {
        String code = null;
        Integer rowNum = 5;

        for (CouponModel coupon : couponList) {
            if (code == null) {
                rowNum = createTableHead(wb, sheet, rowNum, coupon.getCode(), coupon.getCurrencyName());


            } else if (!code.equals(coupon.getCode())) {
                rowNum ++;
                rowNum = createTableHead(wb, sheet, rowNum, coupon.getCode(), coupon.getCurrencyName());
            }
            code = coupon.getCode();
            int cellStartRow = 0;
            Row row = sheet.createRow(rowNum++);
            IncomeService.addCell(wb, row, cellStartRow, coupon.getTimestamp());
            cellStartRow++;

            IncomeService.addCell(wb,row, cellStartRow, coupon.getAmount(), IncomeService.getFontStyle(wb, (short) 9));
            cellStartRow++;

            IncomeService.addCell(row, cellStartRow, coupon.getCurrencyCode() + "/" + coupon.getCurrencyName(), IncomeService.getFontStyle(wb, (short) 9));
            cellStartRow++;

            IncomeService.addCell(wb,row, cellStartRow, coupon.getCourse(), IncomeService.getFontStyle(wb, (short) 9));
            cellStartRow++;

            IncomeService.addCell(row, cellStartRow, AssetType.Bonds.getRevenueCode(), IncomeService.getFontStyle(wb, (short) 9));
            cellStartRow++;

            IncomeService.addCell(wb, row, cellStartRow, coupon.getAmount(), IncomeService.getFontStyle(wb, (short) 9));
            cellStartRow++;
            IncomeService.addCell( row, cellStartRow, ValuteService.getCountry(coupon.getCurrencyCode()), IncomeService.getFontStyle(wb, (short) 9));


        }
    }

    private static @NonNull Integer createTableHead(Workbook wb, Sheet sheet, Integer rowNum, String code, String currency) {
        Row row = sheet.createRow(rowNum);
        Cell cell = row.createCell(0);
        cell.setCellValue(String.format("%s (код страны %s)", code, currency));
        cell.setCellStyle(IncomeService.getCellStyleColor(wb, ExcelHelper.getColor(ColorImp.TABLE_HEAD), IncomeService.getFont(wb, true, (short) 9)));
        sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 0, 12));
        rowNum++;

        row = sheet.createRow(rowNum);
        cell = row.createCell(0);
        cell.setCellValue("Выручка");
        cell.setCellStyle(IncomeService.getCellStyleColor(wb, ExcelHelper.getColor(ColorImp.REVENUE), IncomeService.getFont(wb, (short) 9)));
        sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 0, 6));

        cell = row.createCell(9);
        cell.setCellValue("Налог удержан");
        cell.setCellStyle(IncomeService.getCellStyleColor(wb, ExcelHelper.getColor(ColorImp.EXPENDITURE), IncomeService.getFont(wb, (short) 9)));
        sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 7, 12));
        rowNum++;

        IncomeService.createHead(sheet, rowNum, 0, new String[]{"Дата", "В валюте", "Валюта", "Курс", "Код", "В рублях", "Страна"}, IncomeService.getCellStyleColor(wb, ExcelHelper.getColor(ColorImp.COLUMN_HEAD), IncomeService.getFont(wb, (short) 9)));
        IncomeService.createHead(sheet, rowNum, 7, new String[]{"Ставка", "В валюте", "Дата", "Курс", "В рублях"}, IncomeService.getCellStyleColor(wb, ExcelHelper.getColor(ColorImp.COLUMN_HEAD), IncomeService.getFont(wb, (short) 9)));
        rowNum++;

        return rowNum;
    }
}
