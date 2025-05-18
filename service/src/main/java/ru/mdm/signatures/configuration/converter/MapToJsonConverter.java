package ru.mdm.signatures.configuration.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.r2dbc.postgresql.codec.Json;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

import java.util.Map;

/**
 * Конвертер Map в Json.
 */
@Slf4j
@WritingConverter
@RequiredArgsConstructor
public class MapToJsonConverter implements Converter<Map<String, Object>, Json> {

    private final ObjectMapper objectMapper;

    @Override
    public Json convert(Map<String, Object> source) {
        try {
            return Json.of(objectMapper.writeValueAsString(source));
        } catch (JsonProcessingException e) {
            log.error("Deserialization error: {}", source, e);
        }
        return Json.of("");
    }
}
