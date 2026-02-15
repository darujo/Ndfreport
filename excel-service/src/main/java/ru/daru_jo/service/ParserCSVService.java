package ru.daru_jo.service;

import com.opencsv.CSVReader;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.daru_jo.entity.*;
import ru.daru_jo.model.AssetType;

import java.io.FileReader;
import java.sql.Timestamp;
import java.util.Arrays;

@Slf4j
@Service
public class ParserCSVService {
    private OrderService orderService;
    private RevenueService revenueService;
    private ExpenditureService expenditureService;
    private CouponService couponService;
    private CouponNKDService couponNKDService;
    private CouponNKDMinusService couponNKDMinusService;

    @Autowired
    public void setOrderService(OrderService orderService) {
        this.orderService = orderService;
    }

    @Autowired
    public void setRevenueService(RevenueService revenueService) {
        this.revenueService = revenueService;
    }

    @Autowired
    public void setExpenditureService(ExpenditureService expenditureService) {
        this.expenditureService = expenditureService;
    }

    @Autowired
    public void setCouponNKDService(CouponNKDService couponNKDService) {
        this.couponNKDService = couponNKDService;
    }

    @Autowired
    public void setCouponNKDMinusService(CouponNKDMinusService couponNKDMinusService) {
        this.couponNKDMinusService = couponNKDMinusService;
    }

    @Autowired
    public void setCouponService(CouponService couponService) {
        this.couponService = couponService;
    }

    @Transactional
    public Order readDataLineByLine(String userNik, String file) {

        try {

            // Create an object of filereader
            // class with CSV file as a parameter.
            FileReader filereader = new FileReader(file);

            // create csvReader object passing
            // file reader as a parameter
            CSVReader csvReader = new CSVReader(filereader);
            String[] nextRecord;
            Order order = orderService.saveOrder(new Order(userNik));
            // we are going to read data line by line
            while ((nextRecord = csvReader.readNext()) != null) {
                parserLine(order, nextRecord);

//                for (String cell : nextRecord) {
//
//                    System.out.print(cell + "\t");
//                }
//                System.out.println();
            }
            return order;
        } catch (Exception e) {
            log.error(Arrays.toString(e.getStackTrace()));
        }
        return null;
    }

    private void parserLine(Order order, String[] nextRecord) {
        if (nextRecord.length > 10) {
            if (nextRecord[0].equals("Сделки") || nextRecord[0].equals("Trades")) {
                if (nextRecord[2].equals("Order")) {
                    saveRevenue(order, nextRecord);
                } else if (nextRecord[2].equals("ClosedLot")) {
                    saveExpenditure(order, nextRecord);
                }
            } else if (nextRecord[0].equals("Statement")
                    && nextRecord[1].equals("Data")
                    && nextRecord[2].equals("Period")
            ) {
                String[] period = nextRecord[3].split(" ");
                if (period.length > 3) {
                    order.setYear(period[2]);
                }
            } else if (nextRecord[0].equals("Информация о счете") || nextRecord[0].equals("Account Information")) {
                if (nextRecord[1].equals("Data")
                        && (nextRecord[2].equals("Account")
                        || nextRecord[2].equals("Счет"))) {
                    order.setAccount(nextRecord[3]);
                }
            } else if (nextRecord[0].equals("Проценты по облигациям: получено") || nextRecord[0].equals("Bond Interest Received")) {
                if (nextRecord[4].startsWith("Купонный платеж облигации ")
                        || nextRecord[4].startsWith("Bond Coupon Payment ")) {
                    saveCoupon(order, nextRecord);
                }
                // TODO Правильное русское название
                if (nextRecord[4].startsWith("Начисленные проценты за продажу ")
                        || nextRecord[4].startsWith("Sold Accrued Interest ")) {
                    saveNKDMinusCoupon(order, nextRecord);
                }
                // TODO Правильное русское название

            } else if (nextRecord[0].equals("Выплаченные проценты по облигациям") || nextRecord[0].equals("Bond Interest Paid")) {
// TODO Правильное русское название в двух местах
                if (nextRecord[4].startsWith("Начисленные проценты за покупку ")
                        || nextRecord[4].startsWith("Purchase Accrued Interest ")) {
                    saveNKDCoupon(order, nextRecord);
                }
            }
        }
    }

    /**
     * @param order  заказ
     * @param record массив
     *               Bond Interest Received          ,Header,Currency,Date,Description,Amount,Code
     *               Проценты по облигациям: получено,Header,Валюта  ,Дата,Описание   ,Сумма ,Код
     *               0                               ,1     ,2       ,3   ,4          ,5     ,6
     */

    private void saveCoupon(Order order, String[] record) {
        String code = getCodeCoupon(record[4]);
        Coupon coupon = new Coupon(null,
                code,
                Timestamp.valueOf(record[3]),
                Double.parseDouble(record[5]),
                record[2],
                order);
        couponService.save(coupon);
    }

    private void saveNKDCoupon(Order order, String[] record) {
        String code = record[4].startsWith("Purchase Accrued Interest ")
                ?
                record[4].substring("Purchase Accrued Interest ".length())
                :
                // TODO Правильное русское название в двух местах
                record[4].substring("Начисленные проценты за покупку ".length());
        CouponNKD coupon = new CouponNKD(null,
                code,
                Timestamp.valueOf(record[3]),
                Double.parseDouble(record[5]),
                record[2],
                order);
        couponNKDService.save(coupon);
    }

    private void saveNKDMinusCoupon(Order order, String[] record) {
        String code = record[4].startsWith("Sold Accrued Interest")
                ?
                record[4].substring("Sold Accrued Interest ".length())
                :
                record[4].substring("Начисленные проценты за продажу ".length());
        CouponNKDMinus coupon = new CouponNKDMinus(null,
                code,
                Timestamp.valueOf(record[3]),
                Double.parseDouble(record[5]),
                record[2],
                order);
        couponNKDMinusService.save(coupon);
    }

    private String getCodeCoupon(String s) {
        return s.substring(s.indexOf("("), s.indexOf("-"));
    }

    /**
     *
     * @param order  Заказ
     * @param record Массив:
     *               Trades,Header,DataDiscriminator,Asset Category,Currency,Symbol,Date/Time,Exchange,Quantity,T. Price,C. Price,Proceeds,Comm/Fee,Basis,Realized P/L,MTM P/L,Code,
     *               0,     1,     2,                3,             4,       5,     6,        7,       8,       9,       10,      11,      12,            13,          14,     15,
     *
     */
    private void saveRevenue(Order order, String[] record) {
        if (!record[8].startsWith("-")) {
            return;
        }
        try {
            Revenue revenue = new Revenue(null,
                    AssetType.valueOf(record[3]).getType().toString(),
                    record[3],
                    record[4],
                    record[5],
                    Timestamp.valueOf(record[6].substring(0, 10) + record[6].substring(11)),
                    -1 * Double.parseDouble(record[8]),
                    Double.parseDouble(record[9]),
                    Double.parseDouble(record[12]),
                    order);
            revenueService.save(revenue);
        } catch (NumberFormatException e) {
//            log.error(e.getMessage());
            for (String cell : record) {

                System.out.print(cell + "\t");
            }
            System.out.println();
        }
    }

    /**
     *
     * @param order  Заказ
     * @param record Массив:
     *               Trades,Header,DataDiscriminator,Asset Category,Currency,Symbol,Date/Time,Exchange,Quantity,T. Price,C. Price,Proceeds,Comm/Fee,Basis,Realized P/L,MTM P/L,Code,
     *               0,     1,     2,                3,             4,       5,     6,        7,       8,       9,       10,      11,      12,            13,          14,     15,
     *
     */
    private void saveExpenditure(Order order, String[] record) {
        // Trades,Header,DataDiscriminator,Asset Category,Currency,Symbol,Date/Time,Exchange,Quantity,T. Price,C. Price,Proceeds,Comm/Fee,Basis,Realized P/L,MTM P/L,Code,
        // 0,     1,     2,                3,             4,       5,     6,        7,       8,       9,       10,      11,      12,            13,          14,     15,
        try {
            Expenditure expenditure = new Expenditure(null,
                    AssetType.valueOf(record[3]).getType().toString(),
                    record[3],
                    record[4],
                    record[5],
                    Timestamp.valueOf(record[6] + " 00:00:00"),
                    Double.parseDouble(record[8]),
                    Double.parseDouble(record[9]),
                    record[12].isBlank() ? null : Double.parseDouble(record[12]),
                    order);
            expenditureService.save(expenditure);
        } catch (Exception e) {
//            log.error(e.getMessage());
            for (String cell : record) {

                System.out.print(cell + "\t");
            }
            System.out.println();
        }
    }
}
