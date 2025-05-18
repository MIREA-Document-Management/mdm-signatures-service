package ru.mdm.signatures.model.entity;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

/**
 * Сущность ключей пользователя.
 */
@Data
@Table("mdm_user_keys")
public class UserKeys implements Persistable<String> {

    /**
     * Логин пользователя.
     */
    @Id
    @Column("user_login")
    private String userLogin;

    /**
     * Приватный ключ.
     */
    @Column("private_key")
    private String privateKey;

    /**
     * Публичный ключ.
     */
    @Column("public_key")
    private String publicKey;

    /**
     * Дата и время создания.
     */
    @CreatedDate
    @Column("creation_date")
    private LocalDateTime creationDate;

    @Transient
    private boolean isNew = true;

    @Override
    public String getId() {
        return userLogin;
    }

    @Override
    public boolean isNew() {
        return isNew;
    }
}
