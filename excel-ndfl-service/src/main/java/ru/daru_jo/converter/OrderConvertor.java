package ru.daru_jo.converter;

import ru.daru_jo.dto.OrderDTO;
import ru.daru_jo.entity.Order;

public class OrderConvertor {

    public static OrderDTO getOrderDto(Order order, Boolean pay) {
        return new OrderDTO(order.getId(), order.getUserNik(), order.getTimestamp(), order.getYearList(), pay);
    }
}
