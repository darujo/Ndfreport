package ru.daru_jo.service.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.daru_jo.entity.Bond;
import ru.daru_jo.entity.OrderAccount;
import ru.daru_jo.repository.BondRepository;
import ru.daru_jo.specifications.Specifications;

import java.util.List;

@Service
public class BondService {
    private BondRepository bondRepository;

    @Autowired
    public void setBondRepository(BondRepository bondRepository) {
        this.bondRepository = bondRepository;
    }

    public List<Bond> findAll(OrderAccount orderAccount, String type, Sort sort) {
        Specification<Bond> specification = Specification.unrestricted();
        specification = Specifications.eq(specification,"order", orderAccount);
        specification = Specifications.eq(specification,"type",type);
        return bondRepository.findAll(specification,sort);
    }

    public List<Bond> findAll(OrderAccount orderAccount, List<String> types, Sort sort) {
        Specification<Bond> specification = Specification.unrestricted();
        specification = Specifications.eq(specification,"order", orderAccount);
        specification = Specifications.in(specification,"type",types);
        return bondRepository.findAll(specification,sort);
    }

    public void save(Bond bond) {
        bondRepository.save(bond);
    }
}
