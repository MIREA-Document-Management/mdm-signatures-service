package ru.mdm.signatures.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * REST-API для подписания документов.
 */
public interface DocumentSigningApi {

    String BASE_URI = "/api/v1/documents";

    @Operation(summary = "Подписать документ",
            description = "Подписать документ",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Документ подписан"),
                    @ApiResponse(responseCode = "400", description = "Неверный формат переданных значений",
                            content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера",
                            content = @Content(schema = @Schema(hidden = true)))
            })
    @PostMapping("/{id}/version/{versionId}/sign")
    Mono<String> signDocument(@PathVariable UUID id, @PathVariable UUID versionId);

    @Operation(summary = "Проверить подпись документа",
            description = "Проверить подпись документа",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Документ проверен"),
                    @ApiResponse(responseCode = "400", description = "Неверный формат переданных значений",
                            content = @Content(schema = @Schema(hidden = true))),
                    @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера",
                            content = @Content(schema = @Schema(hidden = true)))
            })
    @PostMapping("/{id}/version/{versionId}/verify")
    Mono<String> verify(@PathVariable UUID id, @PathVariable UUID versionId, @RequestParam String signerLogin);
}
