package ru.daru_jo.repository;

import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.daru_jo.model.User;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<@NonNull User, @NonNull Long>, JpaSpecificationExecutor<@NonNull User> {
    Optional<User> findByNikNameIgnoreCase(String nikName);
    Optional<User> findByNikNameIgnoreCaseAndIdIsNot(String nikName, Long id);
}
