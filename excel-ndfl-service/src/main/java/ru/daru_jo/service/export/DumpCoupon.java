package ru.daru_jo.service.export;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.daru_jo.entity.*;
import ru.daru_jo.helper.ExcelHelper;
import ru.daru_jo.model.Deal;
import ru.daru_jo.model.ExpenditureModel;
import ru.daru_jo.model.RevenueModel;
import ru.daru_jo.service.IncomeService;
import ru.daru_jo.service.db.ValuteService;
import ru.daru_jo.service.db.BondService;
import ru.daru_jo.type.AssetType;
import ru.daru_jo.model.CouponModel;
import ru.daru_jo.type.ColorImp;
import ru.daru_jo.type.OperationType;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class DumpCoupon {
    private BondService bondService;
    private ValuteService valuteService;


    @Autowired
    public void setBondService(BondService bondService) {
        this.bondService = bondService;
    }

    @Autowired
    public void setValuteService(ValuteService valuteService) {
        this.valuteService = valuteService;
    }

    private final Sort sort = Sort.by("orderAccount", "type", "date", "code");

    public void dump(Workbook wb, Sheet sheet, List<OrderAccount> orderAccountList, String year) {
        Row row = sheet.createRow(0);
        Cell cell = row.createCell(0);
        cell.setCellValue(String.format("Раздел 1.2. Доходы по купонам за период 01/01/%s - 31/12/%s", year, year));
        cell.setCellStyle(IncomeService.getCellStyleColor(wb, ExcelHelper.getColor(ColorImp.HEAD), IncomeService.getFont(wb, IndexedColors.WHITE, true, null), HorizontalAlignment.CENTER, VerticalAlignment.CENTER, null));
        sheet.addMergedRegion(new CellRangeAddress(0, 1, 0, 12));

        AtomicInteger rowNum = new AtomicInteger(3);
        orderAccountList.forEach(orderAccount -> {
            Row rowAccount = sheet.createRow(rowNum.getAndIncrement());
            Cell cellAccount = rowAccount.createCell(0);
            cellAccount.setCellValue(String.format("Купоны, полученные в Interactive Brokers %s", orderAccount.getAccount()));
            cellAccount.setCellStyle(IncomeService.getCellStyleColor(wb, ExcelHelper.getColor(ColorImp.CATEGORY), IncomeService.getFont(wb, true, null), HorizontalAlignment.CENTER, VerticalAlignment.CENTER, null));
            sheet.addMergedRegion(new CellRangeAddress(3, 3, 0, 12));

            dump(wb, sheet,rowNum, bondService.findAll(orderAccount, OperationType.COUPON_PAYMENT.toString(), sort).stream().map(coupon -> {
                CouponModel couponModel = new CouponModel(coupon);
                valuteService.updateCurrObject(couponModel);
                return couponModel;
            }).toList());
        });
    }

    public void dump(Workbook wb, Sheet sheet,AtomicInteger rowNum, List<CouponModel> couponList) {
        String code = null;


        for (CouponModel coupon : couponList) {
            if (code == null) {
                rowNum.set( createTableHead(wb, sheet, rowNum.get(), coupon.getCode(), coupon.getCurrencyName()));


            } else if (!code.equals(coupon.getCode())) {

                rowNum.set( createTableHead(wb, sheet, rowNum.incrementAndGet(), coupon.getCode(), coupon.getCurrencyName()));
            }
            code = coupon.getCode();
            int cellStartRow = 0;
            Row row = sheet.createRow(rowNum.getAndIncrement());
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

    public void addCoupon(List<OrderAccount> orderAccountList, Map<String, Map<String, Map<String, Deal>>> mapDeal) {
        orderAccountList.forEach(orderAccount -> {
            Map<String, Deal> dealList = mapDeal
                    .computeIfAbsent(orderAccount.getAccount(), s -> new LinkedHashMap<>())
                    .computeIfAbsent(AssetType.Bonds.getType().toString(), s -> new LinkedHashMap<>());
            List<String> revenueTypes = new ArrayList<>();
            revenueTypes.add(OperationType.COUPON_PAYMENT.toString());
            revenueTypes.add(OperationType.COUPON_NKD.toString());
            revenueTypes.add(OperationType.BOND_FULL_CALL.toString());
            bondService
                    .findAll(orderAccount, revenueTypes, sort)
                    .forEach(coupon -> {
                        Deal deal = dealList.computeIfAbsent(coupon.getCode(), s -> new Deal(coupon.getCode(), new LinkedList<>()));
                        RevenueModel revenueModel = getRevenueModel(coupon);
                        deal.getRevenueList().add(revenueModel);

                    });
            List<String> expenditureTypes = new ArrayList<>();
            expenditureTypes.add(OperationType.COUPON_NKD_MINUS.toString());
            expenditureTypes.add(OperationType.BOND_CLOSED_LOT.toString());

            bondService
                    .findAll(orderAccount, expenditureTypes, sort)
                    .forEach(couponNKDMinus -> {
                        Deal deal = dealList.computeIfAbsent(couponNKDMinus.getCode(), s -> new Deal(couponNKDMinus.getCode(), new LinkedList<>()));
                        ExpenditureModel expenditureModel = getExpenditureModel(couponNKDMinus);
                        if (deal.getRevenueList().isEmpty()) {
                            deal.addExpenditureModelNotRevenue(expenditureModel);
                        } else {
                            deal.getRevenueList().get(0).getExpenditureModelList().add(expenditureModel);
                        }
                    });
        });
    }

    private ExpenditureModel getExpenditureModel(Bond bond) {
        AssetType assetType = AssetType.Bonds;
        OperationType operation = OperationType.valueOf(bond.getType());
        return new ExpenditureModel(operation, bond.getDate(), null, null, bond.getCurrency(), Integer.toString(assetType.getGeneralExpenses()), operation.getText(), bond.getAmount());
    }

    public RevenueModel getRevenueModel(Bond bond) {
        AssetType assetType = AssetType.Bonds;
        OperationType operation = OperationType.valueOf(bond.getType());
        return new RevenueModel(operation, bond.getDate(), null, null, bond.getCurrency(), Integer.toString(assetType.getRevenueCode()), operation.getText(), bond.getAmount());
    }
}
