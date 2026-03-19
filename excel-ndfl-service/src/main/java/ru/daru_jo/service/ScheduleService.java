package ru.daru_jo.service;

import jakarta.activation.MimetypesFileTypeMap;
import jakarta.annotation.PostConstruct;
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
import ru.daru_jo.dto.JavaFileToMultipartFile;
import ru.daru_jo.dto.PayDTO;
import ru.daru_jo.entity.Order;
import ru.daru_jo.entity.OrderAccount;
import ru.daru_jo.integration.PayServiceIntegration;
import ru.daru_jo.model.RunnableNotException;
import ru.daru_jo.service.db.OrderAccountService;
import ru.daru_jo.service.db.OrderService;
import ru.darujo.exceptions.ResourceNotFoundRunTime;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
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
    @PostConstruct
    public void init() {
        try {

            Order order = new Order("Daru");
            orderService.save(order);
            List<MultipartFile> files = new ArrayList<>();
            files.add(new JavaFileToMultipartFile(new File("c:\\11\\csv\\eng.csv")));
            files.add(new JavaFileToMultipartFile(new File("c:\\11\\csv\\eng2.csv")));
            addTaskPars(order ,files);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

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

    public void addTaskPars(Order order, List<MultipartFile> files) {
        executor.schedule(getTaskParsFiles(order, files), 1, TimeUnit.SECONDS);

    }


    public RunnableNotException getTaskParsFiles(Order order, List<MultipartFile> files) {
        Runnable runnable = () -> {
            files.forEach(multipartFile ->
            {
                OrderAccount orderAccount = new OrderAccount();
                orderAccountService.save(orderAccount);
                order.getOrderAccountList().add(orderAccount);
                orderService.save(order);
                try {
                    parserCSVService.readDataLineByLine(orderAccount, new InputStreamReader(multipartFile.getInputStream()));
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
            PayDTO payDTO = payServiceIntegration.sendPay(new PayDTO(null, null, order.getId(), 1990.90, "Оплата заказа " + order.getId(), null, null, null, null));
        };


        return new RunnableNotException(runnable);
    }

    public void getOrderReport(Long orderId, DeferredResult<ResponseEntity<Resource>> deferredResult) {
        Order order = orderService.findById(orderId).orElseThrow(() -> new ResourceNotFoundRunTime("Заказ не найден"));
        getOrderReport(order, deferredResult);

    }

    public void getOrderReport(Order order, DeferredResult<ResponseEntity<Resource>> deferredResult) {
        executor.schedule(getTaskOrderReport(order, deferredResult), 1, TimeUnit.SECONDS);

    }

    @Value("${file.save-report}")
    private String pathSaveReport;

    public RunnableNotException getTaskOrderReport(Order order, DeferredResult<ResponseEntity<Resource>> deferredResult) {
        Runnable runnable = () -> {
            String fileName;
            if(order.getYearList() == null){
                throw new ResourceNotFoundRunTime("Нет данных для отчета");
            }
            if (order.getYearList().size() == 1) {
                fileName = order.getId() + ".xlsx";
                order.getYearList().forEach(year ->
                        incomeService.dump( pathSaveReport + fileName, order, year)
                );
            } else {
                fileName = order.getId() + ".zip";
                FileOutputStream fileOut;
                try {
                    fileOut = new FileOutputStream(pathSaveReport + fileName);
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
                try (ZipOutputStream zipOut = new ZipOutputStream(fileOut)) {
                    order.getYearList().forEach(year -> {
                                String fileExcel = order.getId() + "_" + year + ".xlsx";
                                incomeService.dump(fileExcel, order, year);
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
            }
            MimetypesFileTypeMap fileTypeMap = new MimetypesFileTypeMap();
            String contentType = fileTypeMap.getContentType(fileName);
            File file = new File(pathSaveReport + fileName);
            try {

                InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
                deferredResult.setResult( ResponseEntity.ok()
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
