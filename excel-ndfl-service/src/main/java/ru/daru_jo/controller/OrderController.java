package ru.daru_jo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedModel;
import org.springframework.web.bind.annotation.*;
import ru.daru_jo.converter.OrderConvertor;
import ru.daru_jo.dto.OrderDTO;
import ru.daru_jo.dto.PayDTO;
import ru.daru_jo.entity.Order;
import ru.daru_jo.exceptions.ResourceNotFoundRunTime;
import ru.daru_jo.service.db.OrderService;


@RestController()
@RequestMapping("/v1/order")
public class OrderController {
    private OrderService orderService;

    @Autowired
    public void setOrderService(OrderService orderService) {
        this.orderService = orderService;
    }


    @GetMapping("/{id}")
    public OrderDTO orderDTOEdit(@PathVariable long id) {
        return getOrderDto(orderService.findById(id).orElseThrow(() -> new ResourceNotFoundRunTime("Не найдена разбивка")));
    }


    @DeleteMapping("/{id}")
    public void deleteOrder(@PathVariable long id) {
        orderService.deleteOrder(id);
    }

    @GetMapping("")
    public PagedModel<?> getOrderList(@RequestHeader(required = false) String username,
                                      PagedResourcesAssembler<OrderDTO> assembler) {
        return assembler.toModel(orderService.getOrderList(username).map(this::getOrderDto));
    }

    public OrderDTO getOrderDto(Order order) {
        PayDTO payDTO = orderService.getPay(order);
        return OrderConvertor.getOrderDto(order, payDTO != null && payDTO.getIsCompleted());
    }

}
