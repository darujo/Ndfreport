package ru.daru_jo.repository;

import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import ru.daru_jo.entity.Revenue;


public interface RevenueRepository extends CrudRepository<@NonNull Revenue, @NonNull Long>, JpaSpecificationExecutor<@NonNull Revenue> {
}