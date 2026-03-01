package ru.daru_jo.service.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.daru_jo.entity.OrderAccount;
import ru.daru_jo.entity.Percent;
import ru.daru_jo.repository.PercentRepository;
import ru.daru_jo.specifications.Specifications;

import java.util.List;

@Service
public class PercentService {
    private PercentRepository percentRepository;

    @Autowired
    public void setPercentRepository(PercentRepository percentRepository) {
        this.percentRepository = percentRepository;
    }

    public List<Percent> findAll(OrderAccount orderAccount, String type, Sort sort) {
        Specification<Percent> specification = Specification.unrestricted();
        specification = Specifications.eq(specification,"order", orderAccount);
        specification = Specifications.eq(specification,"type",type);
        return percentRepository.findAll(specification,sort);
    }

    public void save(Percent percent) {
        percentRepository.save(percent);
    }
}
