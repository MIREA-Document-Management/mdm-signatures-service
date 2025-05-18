package ru.mdm.signatures.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import reactor.core.publisher.Mono;
import ru.mdm.documents.client.DocumentsClient;
import ru.mdm.signatures.exception.BadRequestServerException;
import ru.mdm.signatures.exception.ErrorCode;
import ru.mdm.signatures.exception.ResourceNotFoundException;

import java.util.UUID;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
public class DocumentSigningServiceImpl implements DocumentSigningService {

    private final DocumentsClient documentsClient;
    private final ProcessSigningService processSigningService;

    @Override
    public Mono<String> signDocument(UUID id, UUID versionId) {
        return documentsClient.getDocument(id, versionId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(ErrorCode.DOCUMENT_NOT_FOUND)))
                .<UUID>handle((document, sink) -> {
                    if (document.getFileId() == null) {
                        sink.error(new BadRequestServerException(ErrorCode.FILE_IS_ABSENT));
                    } else {
                        sink.next(document.getFileId());
                    }
                })
                .flatMap(fileId -> processSigningService.processSign(id, versionId, fileId))
                .thenReturn("Документ подписан");
    }

    @Override
    public Mono<String> verify(UUID id, UUID versionId, String signerLogin) {
        return documentsClient.getDocument(id, versionId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(ErrorCode.DOCUMENT_NOT_FOUND)))
                .<UUID>handle((document, sink) -> {
                    if (document.getFileId() == null) {
                        sink.error(new BadRequestServerException(ErrorCode.FILE_IS_ABSENT));
                    } else {
                        sink.next(document.getFileId());
                    }
                })
                .flatMap(fileId -> processSigningService.verifyDocument(id, versionId, fileId, signerLogin));
    }
}
