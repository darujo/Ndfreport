package ru.daru_jo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.daru_jo.entity.Expenditure;
import ru.daru_jo.entity.Order;
import ru.daru_jo.repository.ExpenditureRepository;
import ru.daru_jo.specifications.Specifications;

import java.util.List;

@Service
public class ExpenditureService {
    private ExpenditureRepository expenditureRepository;

    @Autowired
    public void setExpenditureRepository(ExpenditureRepository expenditureRepository) {
        this.expenditureRepository = expenditureRepository;
    }

    public void save(Expenditure expenditure){
        expenditureRepository.save(expenditure);
    }

    public List<Expenditure> findAll(Order order){
        Specification<Expenditure> specification = Specification.unrestricted();
        specification = Specifications.eq(specification,"order", order);
        return expenditureRepository.findAll(specification, Sort.by("order").and(Sort.by("category").and(Sort.by("companyName"))));
    }

}
