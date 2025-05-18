package ru.mdm.signatures.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import ru.mdm.signatures.api.DocumentSigningApi;
import ru.mdm.signatures.service.DocumentSigningService;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping(DocumentSigningApi.BASE_URI)
public class DocumentSigningController implements DocumentSigningApi {

    private final DocumentSigningService documentSigningServiceImpl;

    @Override
    public Mono<String> signDocument(UUID id, UUID versionId) {
        return documentSigningServiceImpl.signDocument(id, versionId);
    }

    @Override
    public Mono<String> verify(UUID id, UUID versionId, String signerLogin) {
        return documentSigningServiceImpl.verify(id, versionId, signerLogin);
    }
}
