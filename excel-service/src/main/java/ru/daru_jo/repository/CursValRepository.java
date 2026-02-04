package ru.daru_jo.repository;

import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import ru.daru_jo.entity.CursVal;

public interface CursValRepository extends CrudRepository<@NonNull CursVal, @NonNull Long>, JpaSpecificationExecutor<@NonNull CursVal> {
}