package ru.daru_jo.repository;

import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import ru.daru_jo.entity.Pay;

import java.util.Optional;


public interface PayRepository extends CrudRepository<@NonNull Pay
        , @NonNull Long>, JpaSpecificationExecutor<@NonNull Pay> {
    Optional<Pay> findByOrderId(Long orderId);
}