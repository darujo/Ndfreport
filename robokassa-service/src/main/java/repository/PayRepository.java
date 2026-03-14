package repository;

import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import ru.daru_jo.entity.Pay;


public interface PayRepository extends CrudRepository<@NonNull Pay
        , @NonNull Long>, JpaSpecificationExecutor<@NonNull Pay> {
}