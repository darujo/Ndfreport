package ru.daru_jo.service.db;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.daru_jo.dto.PayDTO;
import ru.daru_jo.entity.Order;
import ru.daru_jo.exceptions.ResourceNotFoundException;
import ru.daru_jo.integration.PayServiceIntegration;
import ru.daru_jo.repository.OrderRepository;
import ru.daru_jo.specifications.Specifications;
import ru.darujo.exceptions.ResourceNotFoundRunTime;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class OrderService {
    private OrderRepository orderRepository;
    private PayServiceIntegration payServiceIntegration;

    @Autowired
    public void setOrderRepository(OrderRepository orderAccountRepository) {
        this.orderRepository = orderAccountRepository;
    }

    @Autowired
    public void setPayServiceIntegration(PayServiceIntegration payServiceIntegration) {
        this.payServiceIntegration = payServiceIntegration;
    }

    public Order save(Order order) {
        return orderRepository.save(order);
    }

    public List<Order> findAll(String userNik) {
        Specification<Order> specification = Specifications.eq(null, "userNik", userNik);
        return orderRepository.findAll(specification, Sort.by(Sort.Direction.DESC, "id"));
    }

    public Optional<Order> findById(long id) {
        return orderRepository.findById(id);
    }

    public void deleteOrder(long id) {
        throw new ResourceNotFoundRunTime("удаление не возможно из-за отсутсвием реализации" + id);

    }

    public Page<Order> getOrderList(String username) {
        Specification<Order> sp = Specification.unrestricted();
        sp = Specifications.eq(sp, "userNik", username);

        return new PageImpl<>(orderRepository.findAll(sp,Sort.by("userNik").and(Sort.by("id"))));
    }

    public PayDTO getPay(Order order) {
        try {
            return payServiceIntegration.getPay(order.getId());
        } catch (ResourceNotFoundException ignore) {
            try {
                if (order.getCount() == null){
                    return null;
                }
                return payServiceIntegration.sendPay(new PayDTO(null, null, order.getId(), getPay(order.getCount()), "Оплата заказа " + order.getId(), null, null, null, null));

            } catch (ResourceNotFoundRunTime ex) {
                log.info(ex.getMessage(), ex);
                return null;
            }
        }

    }
    public static double getPay(Integer count){
        double sum;
        if (count < 100){
            sum =  2000;
        } else if (count < 200){
            sum =  3500;
        } else if (count < 400){
            sum =  4500;
        } else if (count < 600){
            sum =  6000;
        } else if (count < 1000){
            sum =  7500;
        } else if (count < 2000){
            sum =  9500;
        } else if (count < 4000){
            sum =  12500;
        } else {
            sum = 15000;
        }
        return sum;
    }
}