package ru.mdm.signatures.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Модель для публикации события.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignEventModel {

    private String userLogin;

    private UUID documentId;

    private UUID versionId;

    private LocalDateTime signedAt;
}
