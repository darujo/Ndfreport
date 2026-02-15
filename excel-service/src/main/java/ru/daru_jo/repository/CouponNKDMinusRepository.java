package ru.daru_jo.repository;

import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import ru.daru_jo.entity.CouponNKDMinus;


public interface CouponNKDMinusRepository extends CrudRepository<@NonNull CouponNKDMinus, @NonNull Long>, JpaSpecificationExecutor<@NonNull CouponNKDMinus> {
}