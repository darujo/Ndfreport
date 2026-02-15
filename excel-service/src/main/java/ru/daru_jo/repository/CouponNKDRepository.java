package ru.daru_jo.repository;

import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import ru.daru_jo.entity.CouponNKD;


public interface CouponNKDRepository extends CrudRepository<@NonNull CouponNKD, @NonNull Long>, JpaSpecificationExecutor<@NonNull CouponNKD> {
}