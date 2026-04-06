package ru.daru_jo.service.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.daru_jo.entity.OrderAccount;
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

    public List<Revenue> findAll(OrderAccount orderAccount){
        Specification<Revenue> specification = Specification.unrestricted();
        specification = Specifications.eq(specification,"orderAccount", orderAccount);
        return revenueRepository.findAll(specification, Sort.by("orderAccount").and(Sort.by("category").and(Sort.by("companyName").and(Sort.by("timestamp")))));
    }
    public void delete(OrderAccount orderAccount){
        revenueRepository.deleteRevenueByOrderAccount(orderAccount);
    }
}
