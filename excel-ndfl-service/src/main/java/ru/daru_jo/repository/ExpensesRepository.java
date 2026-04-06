package ru.daru_jo.repository;

import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import ru.daru_jo.entity.Expenses;
import ru.daru_jo.entity.OrderAccount;


public interface ExpensesRepository extends CrudRepository<@NonNull Expenses, @NonNull Long>, JpaSpecificationExecutor<@NonNull Expenses> {
    void deleteExpensesByOrderAccount(OrderAccount orderAccount);
}