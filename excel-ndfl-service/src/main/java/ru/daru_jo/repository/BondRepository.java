package ru.daru_jo.repository;

import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import ru.daru_jo.entity.Bond;


public interface BondRepository extends CrudRepository<@NonNull Bond, @NonNull Long>, JpaSpecificationExecutor<@NonNull Bond> {
}