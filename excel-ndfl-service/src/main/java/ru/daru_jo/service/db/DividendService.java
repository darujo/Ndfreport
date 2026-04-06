package ru.daru_jo.service.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.daru_jo.entity.Dividend;
import ru.daru_jo.entity.OrderAccount;
import ru.daru_jo.repository.DividendRepository;
import ru.daru_jo.specifications.Specifications;

import java.util.List;

@Service
public class DividendService {
    private DividendRepository dividendRepository;

    @Autowired
    public void setDividendRepository(DividendRepository dividendRepository) {
        this.dividendRepository = dividendRepository;
    }

    public List<Dividend> findAll(OrderAccount orderAccount, Sort sort) {
        Specification<Dividend> specification = Specification.unrestricted();
        specification = Specifications.eq(specification,"orderAccount", orderAccount);
        return dividendRepository.findAll(specification,sort);
    }

    public void save(Dividend coupon) {
        dividendRepository.save(coupon);
    }

    public void delete(OrderAccount orderAccount){
        dividendRepository.deleteDividendByOrderAccount(orderAccount);
    }
}
