package ru.daru_jo.repository;

import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import ru.daru_jo.entity.Coupon;


public interface CouponRepository extends CrudRepository<@NonNull Coupon, @NonNull Long>, JpaSpecificationExecutor<@NonNull Coupon> {
}