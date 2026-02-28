package ru.daru_jo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import ru.daru_jo.entity.Order;
import ru.daru_jo.service.FileService;
import ru.daru_jo.service.db.OrderService;

import java.util.List;

@Controller
public class fileController {
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

    @GetMapping("")
    public Order saveFile(@RequestParam("file") List<MultipartFile> files,
                          @RequestHeader(required = false) String username){
        Order order = orderService.saveOrder(new Order(username));
        fileService.saveFiles(order, files);
        return order;
    }
}
