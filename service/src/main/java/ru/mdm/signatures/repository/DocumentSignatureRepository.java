package ru.mdm.signatures.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import ru.mdm.signatures.model.entity.DocumentSignature;

import java.util.UUID;

/**
 * Репозиторий для работы с подписанными документами.
 */
@Repository
public interface DocumentSignatureRepository extends ReactiveSortingRepository<DocumentSignature, UUID>, ReactiveCrudRepository<DocumentSignature, UUID> {

    Mono<DocumentSignature> findByDocumentIdAndDocumentVersionIdAndUserLogin(UUID documentId, UUID documentVersionId, String userLogin);
}
