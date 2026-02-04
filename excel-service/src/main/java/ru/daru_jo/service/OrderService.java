package ru.daru_jo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.daru_jo.entity.Order;
import ru.daru_jo.repository.OrderRepository;
import ru.daru_jo.specifications.Specifications;

import java.util.List;

@Service
public class OrderService {
    private OrderRepository orderRepository;

    @Autowired
    public void setOrderRepository(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Order saveOrder (Order order){
        return orderRepository.save(order);
    }

    public List<Order> findAll(String userNik){
        Specification<Order> specification = Specifications.eq(null,"userNik", userNik);
        return orderRepository.findAll(specification, Sort.by(Sort.Direction.DESC,"id"));
    }

    public Order findLast(String userNik){
        return  orderRepository.findDistinctFirstByUserNik(userNik);
    }
}
