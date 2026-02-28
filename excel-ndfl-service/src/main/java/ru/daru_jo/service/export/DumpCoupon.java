package ru.daru_jo.service.export;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.daru_jo.entity.Coupon;
import ru.daru_jo.entity.CouponNKD;
import ru.daru_jo.entity.CouponNKDMinus;
import ru.daru_jo.entity.Order;
import ru.daru_jo.helper.ExcelHelper;
import ru.daru_jo.model.Deal;
import ru.daru_jo.model.ExpenditureModel;
import ru.daru_jo.model.RevenueModel;
import ru.daru_jo.service.IncomeService;
import ru.daru_jo.service.db.CouponNKDMinusService;
import ru.daru_jo.service.db.CouponNKDService;
import ru.daru_jo.service.db.ValuteService;
import ru.daru_jo.service.db.CouponService;
import ru.daru_jo.type.AssetType;
import ru.daru_jo.model.CouponModel;
import ru.daru_jo.type.ColorImp;
import ru.daru_jo.type.OperationType;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Service
public class DumpCoupon {
    private CouponService couponService;
    private CouponNKDService couponNKDService;
    private CouponNKDMinusService couponNKDMinusService;
    private ValuteService valuteService;

    @Autowired
    public void setCouponService(CouponService couponService) {
        this.couponService = couponService;
    }

    @Autowired
    public void setValuteService(ValuteService valuteService) {
        this.valuteService = valuteService;
    }

    @Autowired
    public void setCouponNKDService(CouponNKDService couponNKDService) {
        this.couponNKDService = couponNKDService;
    }

    @Autowired
    public void setCouponNKDMinusService(CouponNKDMinusService couponNKDMinusService) {
        this.couponNKDMinusService = couponNKDMinusService;
    }

    public void dump(Workbook wb, Sheet sheet, Order order) {
        Row row = sheet.createRow(0);
        Cell cell = row.createCell(0);
        cell.setCellValue(String.format("Раздел 1.2. Доходы по купонам за период 01/01/%s - 31/12/%s", order.getYear(), order.getYear()));
        cell.setCellStyle(IncomeService.getCellStyleColor(wb, ExcelHelper.getColor(ColorImp.HEAD), IncomeService.getFont(wb, IndexedColors.WHITE, true, null), HorizontalAlignment.CENTER, VerticalAlignment.CENTER, null));
        sheet.addMergedRegion(new CellRangeAddress(0, 1, 0, 12));
        row = sheet.createRow(3);
        cell = row.createCell(0);
        cell.setCellValue(String.format("Купоны, полученные в Interactive Brokers %s", order.getAccount()));
        cell.setCellStyle(IncomeService.getCellStyleColor(wb, ExcelHelper.getColor(ColorImp.CATEGORY), IncomeService.getFont(wb, true, null), HorizontalAlignment.CENTER, VerticalAlignment.CENTER, null));
        sheet.addMergedRegion(new CellRangeAddress(3, 3, 0, 12));

        dump(wb, sheet, couponService.findAll(order, Sort.by("order", "date", "code")).stream().map(coupon -> {
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
                rowNum++;
                rowNum = createTableHead(wb, sheet, rowNum, coupon.getCode(), coupon.getCurrencyName());
            }
            code = coupon.getCode();
            int cellStartRow = 0;
            Row row = sheet.createRow(rowNum++);
            IncomeService.addCell(wb, row, cellStartRow, coupon.getTimestamp());
            cellStartRow++;

            IncomeService.addCell(wb, row, cellStartRow, coupon.getAmount(), IncomeService.getFontStyle(wb, (short) 9));
            cellStartRow++;

            IncomeService.addCell(row, cellStartRow, coupon.getCurrencyCode() + "/" + coupon.getCurrencyName(), IncomeService.getFontStyle(wb, (short) 9));
            cellStartRow++;

            IncomeService.addCell(wb, row, cellStartRow, coupon.getCourse(), IncomeService.getFontStyle(wb, (short) 9));
            cellStartRow++;

            IncomeService.addCell(row, cellStartRow, AssetType.Bonds.getRevenueCode(), IncomeService.getFontStyle(wb, (short) 9));
            cellStartRow++;

            IncomeService.addCell(wb, row, cellStartRow, coupon.getAmount(), IncomeService.getFontStyle(wb, (short) 9));
            cellStartRow++;
            IncomeService.addCell(row, cellStartRow, ValuteService.getCountry(coupon.getCurrencyCode()), IncomeService.getFontStyle(wb, (short) 9));


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

    public void addCoupon(Order order, Map<String, Map<String, Deal>> mapDeal) {
        Map<String, Deal> dealList = mapDeal.computeIfAbsent(AssetType.Bonds.getType().toString(), s -> new LinkedHashMap<>());
        couponService
                .findAll(order, Sort.by("order", "date", "code"))
                .forEach(coupon -> {
                    Deal deal = dealList.computeIfAbsent(coupon.getCode(), s -> new Deal(coupon.getCode(), new LinkedList<>()));
                    RevenueModel revenueModel = getRevenueModel(coupon);
                    deal.getRevenueList().add(revenueModel);

                });
        couponNKDMinusService
                .findAll(order, Sort.by("order", "code", "date"))
                .forEach(couponNKDMinus -> {
                    Deal deal = dealList.computeIfAbsent(couponNKDMinus.getCode(), s -> new Deal(couponNKDMinus.getCode(), new LinkedList<>()));
                    ExpenditureModel expenditureModel = getExpenditureModel(couponNKDMinus);
                    if (deal.getRevenueList().isEmpty()) {
                        deal.addExpenditureModelNotRevenue(expenditureModel);
                    } else {
                        deal.getRevenueList().get(0).getExpenditureModelList().add(expenditureModel);
                    }
                });
        couponNKDService
                .findAll(order, Sort.by("order", "code", "date"))
                .forEach(couponNKD -> {
                    Deal deal = dealList.computeIfAbsent(couponNKD.getCode(), s -> new Deal(couponNKD.getCode(), new LinkedList<>()));
                    RevenueModel revenueModel = getRevenueModel(couponNKD);
                    deal.getRevenueList().add(revenueModel);

                });
    }

    private RevenueModel getRevenueModel(CouponNKD couponNKD) {
        AssetType assetType = AssetType.Bonds;
        OperationType operation = OperationType.COUPON_PAYMENT;
        return new RevenueModel(operation, couponNKD.getDate(), null, null, couponNKD.getCurrency(), Integer.toString(assetType.getRevenueCode()), operation.getText(), couponNKD.getAmount());

    }

    private ExpenditureModel getExpenditureModel(CouponNKDMinus couponNKDMinus) {
        AssetType assetType = AssetType.Bonds;
        OperationType operation = OperationType.COUPON_NKD_MINUS;
        return new ExpenditureModel(operation, couponNKDMinus.getDate(), null, null, couponNKDMinus.getCurrency(),  Integer.toString(assetType.getGeneralExpenses()), operation.getText(),couponNKDMinus.getAmount());
    }

    public RevenueModel getRevenueModel(Coupon coupon) {
        AssetType assetType = AssetType.Bonds;
        OperationType operation = OperationType.COUPON_PAYMENT;
        return new RevenueModel(operation, coupon.getDate(), null, null, coupon.getCurrency(), Integer.toString(assetType.getRevenueCode()), operation.getText(),coupon.getAmount());
    }
}
