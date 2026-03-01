package ru.daru_jo.repository;

import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import ru.daru_jo.entity.OrderAccount;

public interface OrderAccountRepository extends CrudRepository<@NonNull OrderAccount, @NonNull Long>, JpaSpecificationExecutor<@NonNull OrderAccount> {
}