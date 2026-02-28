package ru.daru_jo.repository;

import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import ru.daru_jo.entity.Dividend;


public interface DividendRepository extends CrudRepository<@NonNull Dividend, @NonNull Long>, JpaSpecificationExecutor<@NonNull Dividend> {
}