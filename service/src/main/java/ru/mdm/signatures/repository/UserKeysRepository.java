package ru.mdm.signatures.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import ru.mdm.signatures.model.entity.UserKeys;

/**
 * Репозиторий для работы с ключами пользователей.
 */
@Repository
public interface UserKeysRepository extends ReactiveSortingRepository<UserKeys, String>, ReactiveCrudRepository<UserKeys, String> {

}
