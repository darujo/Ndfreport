package ru.daru_jo.service.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import ru.daru_jo.entity.Expenses;
import ru.daru_jo.entity.OrderAccount;
import ru.daru_jo.repository.ExpensesRepository;
import ru.daru_jo.specifications.Specifications;

import java.util.List;

@Service
public class ExpensesService {
    private ExpensesRepository expensesRepository;

    @Autowired
    public void setExpensesRepository(ExpensesRepository expensesRepository) {
        this.expensesRepository = expensesRepository;
    }

    public List<Expenses> findAll(OrderAccount orderAccount, String type, Sort sort) {
        Specification<Expenses> specification = Specification.unrestricted();
        specification = Specifications.eq(specification,"order", orderAccount);
        specification = Specifications.eq(specification,"type",type);
        return expensesRepository.findAll(specification,sort);
    }

    public void save(Expenses percent) {
        expensesRepository.save(percent);
    }
}
