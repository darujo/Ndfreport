package ru.daru_jo.service;

import com.opencsv.CSVReader;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.daru_jo.entity.*;
import ru.daru_jo.service.db.*;
import ru.daru_jo.type.AssetType;
import ru.daru_jo.type.OperationType;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.sql.Timestamp;

@Slf4j
@Service
public class ParserCSVService {
    private RevenueService revenueService;
    private ExpenditureService expenditureService;
    private BondService bondService;
    private DividendService dividendService;
    private DividendTaxService dividendTaxService;
    private PercentService percentService;
    private ExpensesService expensesService;
    private OrderAccountService orderAccountService;

    @Autowired
    public void setRevenueService(RevenueService revenueService) {
        this.revenueService = revenueService;
    }

    @Autowired
    public void setExpenditureService(ExpenditureService expenditureService) {
        this.expenditureService = expenditureService;
    }

    @Autowired
    public void setBondService(BondService bondService) {
        this.bondService = bondService;
    }

    @Autowired
    public void setDividendService(DividendService dividendService) {
        this.dividendService = dividendService;
    }

    @Autowired
    public void setDividendTaxService(DividendTaxService dividendTaxService) {
        this.dividendTaxService = dividendTaxService;
    }

    @Autowired
    public void setPercentService(PercentService percentService) {
        this.percentService = percentService;
    }

    @Autowired
    public void setExpensesService(ExpensesService expensesService) {
        this.expensesService = expensesService;
    }

    @Transactional
    public void readDataLineByLine(OrderAccount orderAccount, String file) {
        try {
            FileReader filereader = new FileReader(file);
            readDataLineByLine(orderAccount, filereader);
        } catch (FileNotFoundException e) {
            log.error(e.getMessage(), e);
        }
    }

    @Transactional
    public void readDataLineByLine(OrderAccount orderAccount, Reader reader) {

        try {

            // Create an object of filereader
            // class with CSV file as a parameter.


            // create csvReader object passing
            // file reader as a parameter
            CSVReader csvReader = new CSVReader(reader);
            String[] nextRecord;
            // we are going to read data line by line
            while ((nextRecord = csvReader.readNext()) != null) {
                try {
                    parserLine(orderAccount, nextRecord);
                } catch (Exception e) {
                    orderAccount.setError(e.getMessage());
                    log.error(e.getMessage(), e);
                }

            }
        } catch (Exception e) {
            orderAccount.setError(e.getMessage());
            log.error(e.getMessage(), e);
        }
    }

    boolean loadDividend = false;

    private void parserLine(OrderAccount orderAccount, String[] nextRecord) {
        if (nextRecord.length > 3) {
            if (nextRecord[0].equals("Сделки") || nextRecord[0].equals("Trades")) {
                if (nextRecord[2].equals("Order")) {
                    saveRevenue(orderAccount, nextRecord);
                } else if (nextRecord[2].equals("ClosedLot")) {
                    saveExpenditure(orderAccount, nextRecord);
                }
            } else if (nextRecord[0].equals("Statement")
                    && nextRecord[1].equals("Data")
                    && nextRecord[2].equals("Period")
            ) {
                String[] period = nextRecord[3].split(" ");
                if (period.length > 3) {
                    orderAccount.setYear(period[2]);
                    orderAccountService.save(orderAccount);

                }
            } else if (nextRecord[0].equals("Информация о счете") || nextRecord[0].equals("Account Information")) {
                if (nextRecord[1].equals("Data")
                        && (nextRecord[2].equals("Account")
                        || nextRecord[2].equals("Счет"))) {
                    orderAccount.setAccount(nextRecord[3]);
                    orderAccountService.save(orderAccount);
                }
            } else if (nextRecord[0].equals("Проценты по облигациям: получено") || nextRecord[0].equals("Bond Interest Received")) {
                if (nextRecord[4].startsWith("Купонный платеж облигации ")
                        || nextRecord[4].startsWith("Bond Coupon Payment ")) {
                    saveCoupon(orderAccount, nextRecord);
                }
                // TODO Правильное русское название
                if (nextRecord[4].startsWith("Начисленные проценты за продажу ")
                        || nextRecord[4].startsWith("Sold Accrued Interest ")) {
                    saveNKDMinusCoupon(orderAccount, nextRecord);
                }
                // TODO Правильное русское название

            } else if (nextRecord[0].equals("Выплаченные проценты по облигациям") || nextRecord[0].equals("Bond Interest Paid")) {
                // TODO Правильное русское название в двух местах
                if (nextRecord[4].startsWith("Начисленные проценты за покупку ")
                        || nextRecord[4].startsWith("Purchase Accrued Interest ")) {
                    saveNKDCoupon(orderAccount, nextRecord);
                }
            } else if (nextRecord[0].equals("Dividends")) {
                if (nextRecord[1].equals("Header")
                        && nextRecord[2].equals("Currency")
                        && nextRecord[3].equals("Date")
                        && nextRecord[4].equals("Description")
                        && nextRecord[5].equals("Amount")
                ) {
                    loadDividend = nextRecord.length < 7 || !nextRecord[6].equals("Code");
                } else if (loadDividend && !nextRecord[4].isBlank()) {
                    saveDividend(orderAccount, nextRecord);
                }

            } else if (nextRecord[0].equals("Withholding Tax")) {
                if (!nextRecord[4].isBlank() && !nextRecord[4].equals("Description")) {
                    saveDividendTax(orderAccount, nextRecord);
                }
            } else if (nextRecord[0].equals("Corporate Actions")) {
                saveCorporateAction(orderAccount, nextRecord);
            } else if (nextRecord[0].equals("Interest")) {
                saveInterest(orderAccount, nextRecord);
            } else if (nextRecord[0].equals("Fees")) {
                saveFees(orderAccount, nextRecord);
            } else if (nextRecord[0].equals("Broker Interest Paid")) {
                saveBrokerInterestPaid(orderAccount, nextRecord);
            } else if (nextRecord[0].equals("Transaction Fees")) {
                saveTransactionFees(orderAccount, nextRecord);
            }
        }
    }

    /**
     * 0               ,1     ,2             ,3       ,4                     ,5     ,6           ,7       ,8          ,9       ,10
     * Transaction Fees,Header,Asset Category,Currency,Date/Time             ,Symbol,Description ,Quantity,Trade Price,Amount  ,Code
     * Transaction Fees,Data  ,Stocks        ,GBP     ,"2022-01-25, 06:45:25",EVR   ,EVRAZ PLC   ,303     ,4.938      ,-7.48105,
     * Transaction Fees,Data  ,              ,        ,                      ,      ,UK Stamp Tax,        ,           ,-7.48105,
     */

    private void saveTransactionFees(OrderAccount orderAccount, String[] record) {
        if (record[2].equals("Stocks")) {
            Expenses expenses = new Expenses(
                    record[6],
                    getDateForDateTime(record[4]),
                    Double.parseDouble(record[9]),
                    record[3],
                    orderAccount,
                    OperationType.EXPENSES_TRANSACTION_FEES.toString());
            expensesService.save(expenses);
        }
    }

    /**
     * 0                   ,1     ,2       ,3         ,4                              ,5     ,6
     * Broker Interest Paid,Header,Currency,Date      ,Description                    ,Amount,Code,,,,,,,,,,,
     * Broker Interest Paid,Data  ,EUR     ,2022-01-05,EUR Debit Interest for Dec-2021,-34.76,,,,,,,,,,,,
     */

    private void saveBrokerInterestPaid(OrderAccount orderAccount, String[] record) {
        Expenses expenses = new Expenses(
                record[4],
                getDateForDate(record[3]),
                Double.parseDouble(record[5]),
                record[2],
                orderAccount,
                OperationType.EXPENSES_INTEREST_PAID.toString());
        expensesService.save(expenses);
    }

    /**
     * 0   ,1     ,2         ,3       ,4         ,5                                     ,6
     * Fees,Header,Subtitle  ,Currency,Date      ,Description                           ,Amount
     * Fees,Data  ,Other Fees,USD     ,2022-01-04,K******2K:Global Snapshot for Dec 2021,-0.03
     * Fees,Data  ,Other Fees,USD     ,2022-01-04,K******2K:Global Snapshot for Dec 2021,0.03
     */

    private void saveFees(OrderAccount orderAccount, String[] record) {
        double amount = Double.parseDouble(record[5]);
        if (amount > 0) {
            Percent percent = new Percent(
                    null,
                    record[5],
                    getDateForDate(record[4]),
                    amount,
                    record[3],
                    orderAccount,
                    OperationType.INTEREST_FEES.toString());
            percentService.save(percent);
        } else if (amount < 0) {
            Expenses expenses = new Expenses(
                    record[5],
                    getDateForDate(record[4]),
                    amount,
                    record[3],
                    orderAccount,
                    OperationType.EXPENSES_FEES.toString());
            expensesService.save(expenses);
        }
    }

    /**
     * 0       ,1     ,2       ,3         ,4                              ,5
     * Interest,Header,Currency,Date      ,Description                    ,Amount
     * Interest,Data  ,EUR     ,2022-01-05,EUR Debit Interest for Dec-2021,-34.76
     */

    private void saveInterest(OrderAccount orderAccount, String[] record) {
        Percent percent = new Percent(
                null,
                record[4],
                getDateForDate(record[3]),
                Double.parseDouble(record[5]),
                record[2],
                orderAccount,
                OperationType.INTEREST.toString());
        percentService.save(percent);
    }

    /**
     * 0                ,1     ,2             ,3       ,4          ,5                     ,6                                                                                                                              ,7       ,8       ,9    ,10          ,11
     * Corporate Actions,Header,Asset Category,Currency,Report Date,Date/Time             ,Description                                                                                                                    ,Quantity,Proceeds,Value,Realized P/L,Code
     * Corporate Actions,Data  ,Bonds         ,USD     ,2025-01-02 ,"2025-01-01, 20:25:00","(US81180WAL54) Full Call / Early Redemption for USD 1.00 per Bond (STX 4 3/4 01/01/25 WAL5, STX 4 3/4 01/01/25, US81180WAL54)",-12000  ,12000   ,0    ,-46.66      ,
     * Corporate Actions,Data  ,Bonds         ,USD     ,Closed Lot:,2022-03-14            ,Basis: 2044.56                                                                                                                 ,2000    ,        ,     ,-44.56      ,LT
     */

    private String bondCode;

    private void saveCorporateAction(OrderAccount orderAccount, String[] record) {
        if (record[2].equals("Bonds")) {
            if (record[6].contains("Full Call / Early Redemption")) {
                bondCode = saveBondFullCall(orderAccount, record);
            } else if (record[4].equals("Closed Lot:")) {
                saveBondClosedLot(orderAccount, bondCode, record);
            } else {
                bondCode = null;
            }
        }
    }

    private void saveBondClosedLot(OrderAccount orderAccount, String bondCode, String[] record) {
        Bond bond = new Bond(
                null,
                bondCode,
                getDateForDate(record[5]),
                Double.parseDouble(record[7]),
                record[3],
                orderAccount,
                OperationType.BOND_CLOSED_LOT.toString(),
                Double.parseDouble(record[7])
        );
        bondService.save(bond);
    }

    private String saveBondFullCall(OrderAccount orderAccount, String[] record) {
        String code = getCodeBondFull(record[6]);
        Bond bond = new Bond(
                null,
                code,
                getDateForDateTime(record[5]),
                record[3],
                orderAccount,
                OperationType.BOND_FULL_CALL.toString(),
                Double.parseDouble(record[7])
        );
        bondService.save(bond);
        return code;
    }

    /**
     * @param orderAccount  заказ
     * @param record массив
     *               Dividends,Header,Currency,Date,Description,Amount
     *               0        ,1     ,2       ,3   ,4          ,5
     */
    private void saveDividend(OrderAccount orderAccount, String[] record) {
        Dividend dividend = new Dividend(
                null,
                getCodeDividend(record[4]),
                getDateForDate(record[3]),
                Double.parseDouble(record[5]),
                record[2],
                getCountryDividend(record[4]),
                orderAccount);
        dividendService.save(dividend);
    }

    /**
     * @param orderAccount  заказ
     * @param record массив
     *               Withholding Tax,Header,Currency,Date,Description,Amount,Code
     *               0              ,1     ,2       ,3   ,4          ,5     ,6
     */
    private void saveDividendTax(OrderAccount orderAccount, String[] record) {
        DividendTax dividendTax = new DividendTax(
                null,
                getCodeDividend(record[4]),
                getDateForDate(record[3]),
                Double.parseDouble(record[5]),
                record[2],
                orderAccount);
        dividendTaxService.save(dividendTax);
    }

    private String getCodeDividend(String s) {
        try {


            return s.substring( /*s.indexOf("(") + 1*/ 0, s.indexOf(")") + 1);
        } catch (StringIndexOutOfBoundsException e) {
            log.error(s, e);
            throw e;
        }
    }

    private String getCountryDividend(String s) {
        return s.substring(s.indexOf("(") + 1).substring(0, 2);
    }

    /**
     * @param orderAccount  заказ
     * @param record массив
     *               Bond Interest Received          ,Header,Currency,Date,Description,Amount,Code
     *               Проценты по облигациям: получено,Header,Валюта  ,Дата,Описание   ,Сумма ,Код
     *               0                               ,1     ,2       ,3   ,4          ,5     ,6
     */

    private void saveCoupon(OrderAccount orderAccount, String[] record) {
        String code = getCodeCoupon(record[4]);
        Coupon coupon = new Coupon(null,
                code,
                getDateForDate(record[3]),
                Double.parseDouble(record[5]),
                record[2],
                orderAccount);
        bondService.save(coupon);
    }

    private void saveNKDCoupon(OrderAccount orderAccount, String[] record) {
        String code = record[4].startsWith("Purchase Accrued Interest ")
                ?
                record[4].substring("Purchase Accrued Interest ".length())
                :
                // TODO Правильное русское название в двух местах
                record[4].substring("Начисленные проценты за покупку ".length());
        CouponNKD coupon = new CouponNKD(null,
                code,
                getDateForDate(record[3]),
                Double.parseDouble(record[5]),
                record[2],
                orderAccount);
        bondService.save(coupon);
    }

    private void saveNKDMinusCoupon(OrderAccount orderAccount, String[] record) {
        String code = record[4].startsWith("Sold Accrued Interest")
                ?
                record[4].substring("Sold Accrued Interest ".length())
                :
                record[4].substring("Начисленные проценты за продажу ".length());
        CouponNKDMinus coupon = new CouponNKDMinus(null,
                code,
                getDateForDate(record[3]),
                Double.parseDouble(record[5]),
                record[2],
                orderAccount);
        bondService.save(coupon);
    }

    private String getCodeCoupon(String s) {
        return s.substring(s.indexOf("(") + 1, s.indexOf("-"));
    }

    private String getCodeBondFull(String s) {
        return s.substring(s.indexOf("(") + 1, s.indexOf(")"));
    }

    /**
     *
     * @param orderAccount  Заказ
     * @param record Массив:
     *               Trades,Header,DataDiscriminator,Asset Category,Currency,Symbol,Date/Time,Exchange,Quantity,T. Price,C. Price,Proceeds,Comm/Fee,Basis,Realized P/L,MTM P/L,Code,
     *               0,     1,     2,                3,             4,       5,     6,        7,       8,       9,       10,      11,      12,            13,          14,     15,
     *
     */
    private void saveRevenue(OrderAccount orderAccount, String[] record) {
        if (!record[8].startsWith("-")) {
            return;
        }
        try {
            Revenue revenue = new Revenue(null,
                    AssetType.valueOf(record[3]).getType().toString(),
                    record[3],
                    record[4],
                    record[5],
                    getDateForDateTime(record[6]),
                    -1 * Double.parseDouble(record[8].replace(",", "")),
                    Double.parseDouble(record[9]),
                    Double.parseDouble(record[12]),
                    orderAccount);
            revenueService.save(revenue);
        } catch (NumberFormatException e) {
            for (String cell : record) {

                System.out.print(cell + "\t");
            }
            System.out.println();
            log.error(e.getMessage(), e);

        }
    }

    /**
     *
     * @param orderAccount  Заказ
     * @param record Массив:
     *               Trades,Header,DataDiscriminator,Asset Category,Currency,Symbol,Date/Time,Exchange,Quantity,T. Price,C. Price,Proceeds,Comm/Fee,Basis,Realized P/L,MTM P/L,Code,
     *               0,     1,     2,                3,             4,       5,     6,        7,       8,       9,       10,      11,      12,            13,          14,     15,
     *
     */
    private void saveExpenditure(OrderAccount orderAccount, String[] record) {
        // Trades,Header,DataDiscriminator,Asset Category,Currency,Symbol,Date/Time,Exchange,Quantity,T. Price,C. Price,Proceeds,Comm/Fee,Basis,Realized P/L,MTM P/L,Code,
        // 0,     1,     2,                3,             4,       5,     6,        7,       8,       9,       10,      11,      12,            13,          14,     15,
        try {
            Expenditure expenditure = new Expenditure(null,
                    AssetType.valueOf(record[3]).getType().toString(),
                    record[3],
                    record[4],
                    record[5],
                    getDateForDate(record[6]),
                    Double.parseDouble(record[8].replace(",", "")),
                    Double.parseDouble(record[9]),
                    record[12].isBlank() ? null : Double.parseDouble(record[12]),
                    orderAccount);
            expenditureService.save(expenditure);
        } catch (Exception e) {
            for (String cell : record) {

                System.out.print(cell + "\t");
            }
            System.out.println();
            log.error(e.getMessage(), e);

        }
    }

    private Timestamp getDateForDateTime(String s) {
        return Timestamp.valueOf(s.substring(0, 10) + s.substring(11));
    }

    private Timestamp getDateForDate(String s) {
        return Timestamp.valueOf(s + " 00:00:00");
    }

    @Autowired
    public void setOrderAccountService(OrderAccountService orderAccountService) {
        this.orderAccountService = orderAccountService;
    }
}
