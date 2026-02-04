package ru.daru_jo.repository;

import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import ru.daru_jo.entity.Order;

public interface OrderRepository extends CrudRepository<@NonNull Order, @NonNull Long>, JpaSpecificationExecutor<@NonNull Order> {
    Order findDistinctFirstByUserNik(String userNik);
}