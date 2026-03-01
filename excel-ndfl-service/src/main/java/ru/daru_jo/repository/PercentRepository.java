package ru.daru_jo.repository;

import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import ru.daru_jo.entity.Percent;


public interface PercentRepository extends CrudRepository<@NonNull Percent, @NonNull Long>, JpaSpecificationExecutor<@NonNull Percent> {
}