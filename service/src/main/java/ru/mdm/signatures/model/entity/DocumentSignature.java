package ru.mdm.signatures.model.entity;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Сущность подписанного документа.
 */
@Data
@Table("mdm_document_signatures")
public class DocumentSignature implements Persistable<UUID> {

    /**
     * Идентификатор подписания.
     */
    @Id
    @Column("id")
    private UUID id;

    /**
     * Идентификатор документа.
     */
    @Column("document_id")
    private UUID documentId;

    /**
     * Идентификатор версии документа.
     */
    @Column("document_version_id")
    private UUID documentVersionId;

    /**
     * Логин пользователя.
     */
    @Column("keys_id")
    private String userLogin;

    /**
     * Хэш-сумма документа.
     */
    @Column("document_hash")
    private String documentHash;

    /**
     * Подпись.
     */
    @Column("signature")
    private String signature;

    /**
     * Дата и время подписания.
     */
    @CreatedDate
    @Column("signed_at")
    private LocalDateTime signedAt;

    @Transient
    private boolean isNew = true;

    @Override
    public boolean isNew() {
        return isNew;
    }
}
