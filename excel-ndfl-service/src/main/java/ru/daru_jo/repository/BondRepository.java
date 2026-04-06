package ru.daru_jo.repository;

import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import ru.daru_jo.entity.Bond;
import ru.daru_jo.entity.OrderAccount;


public interface BondRepository extends CrudRepository<@NonNull Bond, @NonNull Long>, JpaSpecificationExecutor<@NonNull Bond> {
    void deleteBondByOrderAccount(OrderAccount orderAccount);
}