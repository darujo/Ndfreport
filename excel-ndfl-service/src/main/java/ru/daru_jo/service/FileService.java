package ru.daru_jo.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
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

    public List<File> saveFiles(Order order , List<MultipartFile> multipartFiles) {
        List<File> files = new ArrayList<>();

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
                            log.error( e.getMessage(),e);

                        }
                    }
            );
        }
        scheduleService.getTaskParsFiles(order,multipartFiles).run();
        return files;
    }
}
