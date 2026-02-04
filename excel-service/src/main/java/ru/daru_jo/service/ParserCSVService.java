package ru.daru_jo.service;

import com.opencsv.CSVReader;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.daru_jo.entity.Expenditure;
import ru.daru_jo.entity.Order;
import ru.daru_jo.entity.Revenue;

import java.io.FileReader;
import java.sql.Timestamp;
import java.util.Arrays;

@Slf4j
@Service
public class ParserCSVService {
    private OrderService orderService;

    @Autowired
    public void setOrderService(OrderService orderService) {
        this.orderService = orderService;
    }

    private RevenueService revenueService;

    @Autowired
    public void setRevenueService(RevenueService revenueService) {
        this.revenueService = revenueService;
    }

    private ExpenditureService expenditureService;

    @Autowired
    public void setExpenditureService(ExpenditureService expenditureService) {
        this.expenditureService = expenditureService;
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
            Order order = orderService.saveOrder(new Order(null, userNik, new Timestamp(System.currentTimeMillis())));
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
            }
        }
    }

    private void saveRevenue(Order order, String[] record) {
        // Trades,Header,DataDiscriminator,Asset Category,Currency,Symbol,Date/Time,Exchange,Quantity,T. Price,C. Price,Proceeds,Comm/Fee,Basis,Realized P/L,MTM P/L,Code,
        // 0,     1,     2,                3,             4,       5,     6,        7,       8,       9,       10,      11,      12,            13,          14,     15,
        if (!record[8].startsWith("-")) {
            return;
        }
        try {


            Revenue revenue = new Revenue(null,
                    record[3],
                    record[4],
                    record[5],
                    Timestamp.valueOf(record[6].substring(0, 10) + record[6].substring(11)),
                    -1 * Integer.parseInt(record[8]),
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

    private void saveExpenditure(Order order, String[] record) {
        // Trades,Header,DataDiscriminator,Asset Category,Currency,Symbol,Date/Time,Exchange,Quantity,T. Price,C. Price,Proceeds,Comm/Fee,Basis,Realized P/L,MTM P/L,Code,
        // 0,     1,     2,                3,             4,       5,     6,        7,       8,       9,       10,      11,      12,            13,          14,     15,
        try {
            Expenditure expenditure = new Expenditure(null,
                    record[3],
                    record[4],
                    record[5],
                    Timestamp.valueOf(record[6] + " 00:00:00"),
                    Integer.parseInt(record[8]),
                    Double.parseDouble(record[9]),
//                Float.parseFloat(record[12]),
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
