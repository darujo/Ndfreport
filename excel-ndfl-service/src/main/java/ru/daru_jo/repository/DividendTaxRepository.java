package ru.daru_jo.repository;

import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import ru.daru_jo.entity.DividendTax;


public interface DividendTaxRepository extends CrudRepository<@NonNull DividendTax, @NonNull Long>, JpaSpecificationExecutor<@NonNull DividendTax> {
}