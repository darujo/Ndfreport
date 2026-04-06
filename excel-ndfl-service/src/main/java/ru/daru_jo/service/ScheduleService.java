package ru.daru_jo.service;

import jakarta.activation.MimetypesFileTypeMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.multipart.MultipartFile;
import ru.daru_jo.dto.PayDTO;
import ru.daru_jo.entity.Order;
import ru.daru_jo.entity.OrderAccount;
import ru.daru_jo.exceptions.ResourceNotFoundRunTime;
import ru.daru_jo.integration.PayServiceIntegration;
import ru.daru_jo.model.RunnableNotException;
import ru.daru_jo.service.db.OrderAccountService;
import ru.daru_jo.service.db.OrderService;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
@Service
public class ScheduleService implements AutoCloseable {
    private ParserCSVService parserCSVService;
    private OrderAccountService orderAccountService;
    private OrderService orderService;
    private PayServiceIntegration payServiceIntegration;
    private IncomeService incomeService;

    @Autowired
    public void setIncomeService(IncomeService incomeService) {
        this.incomeService = incomeService;
    }

    @Autowired
    public void setPayServiceIntegration(PayServiceIntegration payServiceIntegration) {
        this.payServiceIntegration = payServiceIntegration;
    }

    @Autowired
    public void setParserCSVService(ParserCSVService parserCSVService) {
        this.parserCSVService = parserCSVService;
    }

    @Autowired
    public void setOrderAccountService(OrderAccountService orderAccountService) {
        this.orderAccountService = orderAccountService;
    }

    @Autowired
    public void setOrderService(OrderService orderService) {
        this.orderService = orderService;
    }

    ScheduledExecutorService executor = Executors.newScheduledThreadPool(3);

    //    @PostConstruct
//    public void init() {
//        try {
//
//            Order order = new Order("Daru");
//            orderService.save(order);
//            List<MultipartFile> files = new ArrayList<>();
//            files.add(new JavaFileToMultipartFile(new File("c:\\11\\csv\\eng.csv")));
//            files.add(new JavaFileToMultipartFile(new File("c:\\11\\csv\\eng2.csv")));
//            addTaskPars(order, files);
//        } catch (Exception e) {
//            log.error(e.getMessage(), e);
//        }
//    }
//    public void addTaskPars(Order order, List<MultipartFile> files) {
//        executor.schedule(getTaskParsFiles(order, files), 1, TimeUnit.SECONDS);
//
//    }

    @Override
    public void close() {
        // Корректное завершение
        executor.shutdown();
        try {
            while (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
                log.info("ScheduleService stop");
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
        }
    }


    public RunnableNotException getTaskParsFiles(Order order, List<MultipartFile> files) {
        Runnable runnable = () -> {
            AtomicInteger count = new AtomicInteger();
            files.parallelStream().forEach(multipartFile ->
            {
                OrderAccount orderAccount = new OrderAccount();
                orderAccount.setOrder(order);
                orderAccountService.save(orderAccount);
                order.getOrderAccountList().add(orderAccount);
                orderService.save(order);
                try {
                    count.set(count.get() + parserCSVService.readDataLineByLine(orderAccount, new InputStreamReader(multipartFile.getInputStream())));
                    if (order.getYearList() == null) {
                        order.setYearList(new ArrayList<>());
                    }
                    if (!order.getYearList().contains(orderAccount.getYear())) {
                        order.getYearList().add(orderAccount.getYear());
                    }

                    orderService.save(order);


                } catch (IOException e) {
                    orderAccount.setError(e.getMessage());
                    orderAccountService.save(orderAccount);
                    throw new RuntimeException(e);
                }
            });
            order.setCount(count.get());
            orderService.save(order);
            try {
                payServiceIntegration.sendPay(new PayDTO(null, null, order.getId(), OrderService.getPay(count.get()), "Оплата заказа " + order.getId(), null, null, null, null));
            } catch (ResourceNotFoundRunTime ex) {
                log.error(ex.getMessage(), ex);
                throw new ResourceNotFoundRunTime("Сервис оплаты не доступен попробуйте позже");
            }
        };


        return new RunnableNotException(runnable);
    }


    public void getOrderReport(String username, List<Long> orderIdList, DeferredResult<ResponseEntity<Resource>> deferredResult) {
        List<Order> orderList;
        if (orderIdList == null || orderIdList.isEmpty()) {
            orderList = orderService.findAll(username);
        } else {
            orderList = orderIdList.stream().map(id -> orderService.findById(id).orElseThrow(() -> new ResourceNotFoundRunTime("Не найден заказ с id =" + id))).toList();
        }
        orderList = orderList.stream().filter(order ->
                orderService.getPay(order).getIsCompleted()).toList();

        // todo убрать
        if (orderList.isEmpty()) {
            deferredResult.setErrorResult(new ResourceNotFoundRunTime("Заказ еще не оплачен."));
        } else {
            Map<String,Map<String,OrderAccount>> yearAccountOrder = new HashMap<>();
            orderAccountService
                    .findAll(orderList, null)
                    .forEach(orderAccount ->{
                        Map<String, OrderAccount> accountMap = yearAccountOrder.computeIfAbsent(orderAccount.getYear(), k -> new HashMap<>());
                        OrderAccount orderAccountSave = accountMap.get(orderAccount.getAccount());
                        if(orderAccountSave == null || orderAccountSave.getOrder().getId() < orderAccount.getOrder().getId()){
                            accountMap.put(orderAccount.getAccount(), orderAccount);
                        }

                    } );
            List<OrderAccount> orderAccountList =  new ArrayList<>();
            yearAccountOrder.values().forEach(accountOrderAccountMap -> orderAccountList.addAll(accountOrderAccountMap.values()) );
            getOrderReport(orderAccountList, deferredResult);
        }
    }

    public void getOrderReport(List<OrderAccount> orderAccountList, DeferredResult<ResponseEntity<Resource>> deferredResult) {
        executor.schedule(getTaskOrderReport(orderAccountList, null, deferredResult), 1, TimeUnit.SECONDS);

    }

    @Value("${file.save-report}")
    private String pathSaveReport;

    public RunnableNotException getTaskOrderReport(List<OrderAccount> orderAccountList, List<String> years, DeferredResult<ResponseEntity<Resource>> deferredResult) {
        Runnable runnable = () -> {
            String fileName;
            Set<String> yearSet = new HashSet<>();
            if (years != null && !years.isEmpty()) {
                yearSet.addAll(years);
            } else {
                orderAccountList.forEach(orderAccount -> yearSet.add(orderAccount.getYear()));
            }
            if (yearSet.isEmpty()) {
                throw new ResourceNotFoundRunTime("Нет данных для отчета");
            }

            fileName = "ndfl.zip";
            FileOutputStream fileOut;
            try {
                fileOut = new FileOutputStream(pathSaveReport + fileName);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            try (ZipOutputStream zipOut = new ZipOutputStream(fileOut)) {
                yearSet.forEach(year -> {
                            String fileExcel = year + ".xlsx";
                            incomeService.dump(pathSaveReport + fileExcel, orderAccountList, year);
                            ZipEntry ze = new ZipEntry(fileExcel);
                            try {
                                zipOut.putNextEntry(ze);

                                Files.copy(Path.of(pathSaveReport + fileExcel), zipOut);
                                zipOut.closeEntry();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                );
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            MimetypesFileTypeMap fileTypeMap = new MimetypesFileTypeMap();
            String contentType = fileTypeMap.getContentType(fileName);
            log.info(contentType);
            File file = new File(pathSaveReport + fileName);
            try {

                InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
                deferredResult.setResult(ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                "attachment; filename=\"" + fileName + "\"")
                        .contentLength(file.length())
                        .body(resource));
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }

        };

        return new RunnableNotException(runnable);
    }
}
