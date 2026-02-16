package ru.daru_jo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.daru_jo.entity.Order;
import ru.daru_jo.entity.Revenue;
import ru.daru_jo.repository.RevenueRepository;
import ru.daru_jo.specifications.Specifications;

import java.util.List;

@Service
public class RevenueService {
    private RevenueRepository revenueRepository;

    @Autowired
    public void setRevenueRepository(RevenueRepository revenueRepository) {
        this.revenueRepository = revenueRepository;
    }

    public void save(Revenue revenue){
        revenueRepository.save(revenue);
    }

    public List<Revenue> findAll(Order order){
        Specification<Revenue> specification = Specification.unrestricted();
        specification = Specifications.eq(specification,"order", order);
        return revenueRepository.findAll(specification, Sort.by("order").and(Sort.by("category").and(Sort.by("companyName").and(Sort.by("timestamp")))));
    }

}
