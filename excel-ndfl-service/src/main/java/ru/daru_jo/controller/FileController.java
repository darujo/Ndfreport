package ru.daru_jo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.multipart.MultipartFile;
import ru.daru_jo.entity.Order;
import ru.daru_jo.service.FileService;
import ru.daru_jo.service.db.OrderService;

import java.util.List;

@RestController
@RequestMapping("/v1/order")
public class FileController {
    private FileService fileService;
    private OrderService orderService;

    @Autowired
    public void setFileService(FileService fileService) {
        this.fileService = fileService;
    }

    @Autowired
    public void setOrderService(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("")
    public Order saveFile(@RequestPart("file") List<MultipartFile> files,
                          @RequestHeader(required = false) String username){
        Order order = orderService.save(new Order(username));
        fileService.saveFiles(order, files);
        return order;
    }

    @GetMapping("/document")
    public DeferredResult<ResponseEntity<Resource>> asyncDownload(@RequestParam(required = false) List<Long> orderId,
                                                                  @RequestHeader(required = false) String username) {
        DeferredResult<ResponseEntity<Resource>> deferredResult = new DeferredResult<>(30000L); // 30-секундный таймаут
        fileService.getOrderReport(username, orderId, deferredResult);
        return deferredResult;
    }


}
