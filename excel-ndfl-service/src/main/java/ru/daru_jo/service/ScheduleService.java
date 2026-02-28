package ru.daru_jo.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.daru_jo.entity.Order;
import ru.daru_jo.model.RunnableNotException;
import ru.daru_jo.service.db.OrderService;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class ScheduleService implements AutoCloseable {
    private ParserCSVService parserCSVService;
    private OrderService orderService;

    @Autowired
    public void setParserCSVService(ParserCSVService parserCSVService) {
        this.parserCSVService = parserCSVService;
    }

    ScheduledExecutorService executor = Executors.newScheduledThreadPool(3);

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
        Runnable runnable = () ->
                files.forEach(multipartFile ->
                {
                    try {
                        parserCSVService.readDataLineByLine(order, new InputStreamReader(multipartFile.getInputStream()));
                    } catch (IOException e) {
                        order.setError(e.getMessage());
                        orderService.saveOrder(order);
                        throw new RuntimeException(e);
                    }
                });
        return new RunnableNotException(runnable);
    }

    @Autowired
    public void setOrderService(OrderService orderService) {
        this.orderService = orderService;
    }
}
