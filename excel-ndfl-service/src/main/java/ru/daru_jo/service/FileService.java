package ru.daru_jo.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.multipart.MultipartFile;
import ru.daru_jo.entity.Order;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class FileService {
    @Value("${file.save-into}")
    private String pathSave;
    private ScheduleService scheduleService;

    @Autowired
    public void setScheduleService(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    public List<File> saveFiles(Order order, List<MultipartFile> multipartFiles) {
        List<File> files = new ArrayList<>();
        File directory = new File(pathSave + "/" + order.getId());
        boolean created = directory.mkdir();
        if (created) {
            log.info("Директория успешно создана {}/{}", pathSave, order.getId());
        } else {
            log.info("Не удалось создать директорию{}/{}", pathSave, order.getId());
        }
        AtomicInteger num = new AtomicInteger();
        if (multipartFiles != null) {
            multipartFiles.forEach(multipartFile -> {
                        if (multipartFile.getOriginalFilename() == null) {
                            num.getAndIncrement();
                        }
                        String fileName = pathSave + "/" + order.getId() + "/" + (multipartFile.getOriginalFilename() == null ? "update" + num : multipartFile.getOriginalFilename());
                        try (FileOutputStream fileOutputStream = new FileOutputStream(fileName)) {
                            fileOutputStream.write(multipartFile.getBytes());
                            File file = new File(fileName);
                            files.add(file);
                        } catch (IOException e) {
                            log.error(e.getMessage(), e);

                        }
                    }
            );
        }
        scheduleService.getTaskParsFiles(order, multipartFiles).run();
        return files;
    }

    public void getOrderReport(String username, List<Long> orderId, DeferredResult<ResponseEntity<Resource>> deferredResult ){
        scheduleService.getOrderReport(username, orderId, deferredResult);
    }
}
