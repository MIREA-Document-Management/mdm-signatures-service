package ru.mdm.signatures.service;

import jakarta.validation.constraints.NotNull;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * Сервис для подписания документов.
 */
public interface DocumentSigningService {

    /**
     * Подписать документ.
     *
     * @param id идентификатор документа
     * @param versionId идентификатор версии документа
     */
    Mono<String> signDocument(@NotNull UUID id, @NotNull UUID versionId);

    /**
     * Проверить подпись документа.
     *
     * @param id идентификатор документа
     * @param versionId идентификатор версии документа
     * @param signerLogin логин пользователя, которого надо проверить
     */
    Mono<String> verify(@NotNull UUID id, @NotNull UUID versionId, @NotNull String signerLogin);
}
