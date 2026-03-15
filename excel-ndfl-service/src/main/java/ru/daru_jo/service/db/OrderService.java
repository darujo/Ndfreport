package ru.daru_jo.service.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.daru_jo.entity.Order;
import ru.daru_jo.repository.OrderRepository;
import ru.daru_jo.specifications.Specifications;
import ru.darujo.exceptions.ResourceNotFoundRunTime;

import java.util.List;
import java.util.Optional;

@Service
public class OrderService {
    private OrderRepository orderRepository;

    @Autowired
    public void setOrderRepository(OrderRepository orderAccountRepository) {
        this.orderRepository = orderAccountRepository;
    }

    public Order save(Order order){
        return orderRepository.save(order);
    }

    public List<Order> findAll(String userNik){
        Specification<Order> specification = Specifications.eq(null,"userNik", userNik);
        return orderRepository.findAll(specification, Sort.by(Sort.Direction.DESC,"id"));
    }

    public Optional<Order> findById(long id) {
        return orderRepository.findById(id);
    }

    public void deleteOrder(long id) {
        throw new ResourceNotFoundRunTime("удаление не возможно из-за отсутсвием реализации" + id);

    }

    public Page<Order> getOrderList(String username) {
        Specification<Order> sp = Specification.unrestricted();
        sp = Specifications.eq(sp,"userNik", username);
        return new PageImpl<>(orderRepository.findAll(sp));
    }
}
