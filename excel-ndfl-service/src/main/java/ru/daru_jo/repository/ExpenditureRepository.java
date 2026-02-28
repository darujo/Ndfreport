package ru.daru_jo.repository;

import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import ru.daru_jo.entity.Expenditure;

public interface ExpenditureRepository extends CrudRepository<@NonNull Expenditure, @NonNull Long>, JpaSpecificationExecutor<@NonNull Expenditure> {
}