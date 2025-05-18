package ru.mdm.signatures.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.mdm.files.client.FilesClient;
import ru.mdm.kafka.service.KafkaService;
import ru.mdm.signatures.exception.ErrorCode;
import ru.mdm.signatures.exception.ResourceNotFoundException;
import ru.mdm.signatures.exception.ServerException;
import ru.mdm.signatures.model.SignEventModel;
import ru.mdm.signatures.model.entity.DocumentSignature;
import ru.mdm.signatures.model.entity.UserKeys;
import ru.mdm.signatures.repository.DocumentSignatureRepository;
import ru.mdm.signatures.repository.UserKeysRepository;
import ru.mdm.signatures.service.event.DocumentVerifyFailed;
import ru.mdm.signatures.service.event.DocumentVerifySuccess;
import ru.mdm.signatures.service.event.SignDocumentSuccess;
import ru.mdm.signatures.util.CryptoUtil;
import ru.mdm.signatures.util.SecurityContextHolder;

import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessSigningService {

    private final UserKeysRepository keysRepository;
    private final DocumentSignatureRepository documentSignatureRepository;
    private final FilesClient filesClient;
    private final KafkaService kafkaService;
    private final TransactionalOperator txOperator;

    @Value("${mdm.signatures.secret-key}")
    private String secretKey;

    public Mono<Void> processSign(UUID docId, UUID docVersionId, UUID docFileId) {
        return SecurityContextHolder.getUserLogin()
                .flatMap(keysRepository::findById)
                .switchIfEmpty(Mono.defer(this::initKeys))
                .flatMap(keys -> filesClient.getFileContent(docFileId)
                        .flatMap(content -> calculateHash(content.getContent()))
                        .map(hash -> signHashAndMapToEntity(hash, keys, docId, docVersionId)))
                .flatMap(documentSignatureRepository::save)
                .map(entity -> new SignEventModel(entity.getUserLogin(), entity.getDocumentId(), entity.getDocumentVersionId(), entity.getSignedAt()))
                .flatMap(kafkaModel -> kafkaService.sendEvent(SignDocumentSuccess.of(kafkaModel)).thenReturn(kafkaModel))
                .as(txOperator::transactional)
                .then();
    }

    public Mono<String> verifyDocument(UUID docId, UUID docVersionId, UUID docFileId, String signerLogin) {
        return documentSignatureRepository.findByDocumentIdAndDocumentVersionIdAndUserLogin(docId, docVersionId, signerLogin)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException(ErrorCode.USER_IS_NOT_SIGNER)))
                .zipWith(keysRepository.findById(signerLogin))
                .flatMap(objects -> {
                    DocumentSignature documentSignature = objects.getT1();
                    UserKeys userKeys = objects.getT2();

                    return filesClient.getFileContent(docFileId)
                            .flatMap(content -> calculateHash(content.getContent()))
                            .map(actualHash -> {
                                byte[] expectedHash = Base64.getDecoder().decode(documentSignature.getDocumentHash());

                                boolean hashesMatch = MessageDigest.isEqual(actualHash, expectedHash);
                                if (!hashesMatch) {
                                    return false;
                                }

                                PublicKey publicKey = loadPublicKey(userKeys.getPublicKey());
                                try {
                                    Signature verifier = Signature.getInstance("NONEwithRSA");
                                    verifier.initVerify(publicKey);
                                    verifier.update(actualHash);

                                    byte[] signatureBytes = Base64.getDecoder().decode(documentSignature.getSignature());
                                    return verifier.verify(signatureBytes);

                                } catch (Exception e) {
                                    throw new ServerException("Signature verification failed", e);
                                }
                            })
                            .flatMap(verified -> {
                                SignEventModel eventModel = new SignEventModel(signerLogin, docId, docVersionId, documentSignature.getSignedAt());
                                if (verified) {
                                    return kafkaService.sendEvent(DocumentVerifySuccess.of(eventModel))
                                            .thenReturn("Подпись действительна");
                                } else {
                                    return kafkaService.sendEvent(DocumentVerifyFailed.of(eventModel))
                                            .thenReturn("Подпись недействительна");
                                }
                            });

                });
    }

    private Mono<UserKeys> initKeys() {
        return SecurityContextHolder.getUserLogin()
                .doOnNext(login -> log.info("Start generating keys for user_login={}", login))
                .map(login -> {
                    try {
                        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
                        KeyPair keyPair = keyGen.generateKeyPair();
                        String publicKey = Base64.getEncoder().encodeToString(keyPair.getPublic().getEncoded());
                        String privateKey = Base64.getEncoder().encodeToString(keyPair.getPrivate().getEncoded());

                        UserKeys userKeys = new UserKeys();
                        userKeys.setUserLogin(login);
                        userKeys.setPublicKey(publicKey);
                        userKeys.setPrivateKey(CryptoUtil.encrypt(privateKey, secretKey));
                        return userKeys;
                    } catch (NoSuchAlgorithmException e) {
                        throw new ServerException(e.getMessage(), e);
                    }
                })
                .flatMap(keysRepository::save);
    }

    private Mono<byte[]> calculateHash(Flux<DataBuffer> dataBufferFlux) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            return dataBufferFlux
                    .doOnNext(dataBuffer -> {
                        byte[] bytes = new byte[dataBuffer.readableByteCount()];
                        dataBuffer.read(bytes);
                        digest.update(bytes);
                    })
                    .then(Mono.fromCallable(digest::digest));

        } catch (NoSuchAlgorithmException e) {
            return Mono.error(e);
        }
    }

    private DocumentSignature signHashAndMapToEntity(byte[] hash, UserKeys keys, UUID docId, UUID versionId) {
        try {
            // Создание экземпляра объекта Signature с алгоритмом "NONEwithRSA".
            // "NONEwithRSA" означает, что данные уже предварительно хэшированы,
            // и требуется только их шифрование с использованием закрытого ключа RSA.
            Signature signature = Signature.getInstance("NONEwithRSA");

            // Инициализация объекта Signature для подписания с помощью закрытого ключа пользователя.
            signature.initSign(loadPrivateKey(keys.getPrivateKey()));

            // Передача предварительно вычисленного хэш-значения в объект Signature для подписи.
            signature.update(hash);

            // Генерация цифровой подписи.
            byte[] signed = signature.sign();

            // Кодирование полученной подписи в формат Base64 для последующей передачи или хранения.
            String encodedSignature = Base64.getEncoder().encodeToString(signed);

            DocumentSignature documentSignature = new DocumentSignature();
            documentSignature.setDocumentId(docId);
            documentSignature.setDocumentVersionId(versionId);
            documentSignature.setUserLogin(keys.getUserLogin());
            documentSignature.setDocumentHash(Base64.getEncoder().encodeToString(hash));
            documentSignature.setSignature(encodedSignature);
            return documentSignature;
        } catch (Exception e) {
            throw new ServerException("Failed to sign hash", e);
        }
    }

    private PrivateKey loadPrivateKey(String encryptedBase64) {
        try {
            String decryptedBase64 = CryptoUtil.decrypt(encryptedBase64, secretKey);
            byte[] keyBytes = Base64.getDecoder().decode(decryptedBase64);

            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(spec);

        } catch (Exception e) {
            throw new ServerException("Failed to load private key", e);
        }
    }

    private PublicKey loadPublicKey(String base64EncodedPublicKey) {
        try {
            byte[] keyBytes = Base64.getDecoder().decode(base64EncodedPublicKey);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(keySpec);
        } catch (Exception e) {
            throw new ServerException("Failed to load public key", e);
        }
    }
}
