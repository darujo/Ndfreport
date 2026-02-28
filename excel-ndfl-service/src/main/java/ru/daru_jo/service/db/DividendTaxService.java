package ru.daru_jo.service.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.daru_jo.entity.DividendTax;
import ru.daru_jo.entity.Order;
import ru.daru_jo.repository.DividendTaxRepository;
import ru.daru_jo.specifications.Specifications;

import java.sql.Timestamp;
import java.util.List;

@Service
public class DividendTaxService {
    private DividendTaxRepository dividendTaxRepository;

    @Autowired
    public void setDividendTaxRepository(DividendTaxRepository dividendTaxRepository) {
        this.dividendTaxRepository = dividendTaxRepository;
    }

    public List<DividendTax> findAll(Order order, String code, Timestamp date, Sort sort) {
        Specification<DividendTax> specification = Specification.unrestricted();
        specification = Specifications.eq(specification,"order",order);
        specification = Specifications.eq(specification,"code",code);
        specification = Specifications.eq(specification,"date",date);
        return dividendTaxRepository.findAll(specification,sort);
    }

    public void save(DividendTax coupon) {
        dividendTaxRepository.save(coupon);
    }
}
