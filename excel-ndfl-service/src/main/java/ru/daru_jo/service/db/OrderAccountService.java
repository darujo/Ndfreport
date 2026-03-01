package ru.daru_jo.service.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.daru_jo.entity.Order;
import ru.daru_jo.entity.OrderAccount;
import ru.daru_jo.repository.OrderAccountRepository;
import ru.daru_jo.specifications.Specifications;

import java.util.List;

@Service
public class OrderAccountService {
    private OrderAccountRepository orderAccountRepository;

    @Autowired
    public void setOrderRepository(OrderAccountRepository orderAccountRepository) {
        this.orderAccountRepository = orderAccountRepository;
    }

    public OrderAccount save(OrderAccount orderAccount){
        return orderAccountRepository.save(orderAccount);
    }

    public List<OrderAccount> findAll(Order order,String year){
        Specification<OrderAccount> specification = Specifications.eq(null,"order", order);
        specification = Specifications.eq(specification,"year", year);
        return orderAccountRepository.findAll(specification, Sort.by("order", "year", "account"));
    }
}
