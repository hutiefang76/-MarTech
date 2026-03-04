package cn.javahome.frank.cdp.api.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;
import java.util.Map;

public record StandardizedRecord(
        @NotBlank String tenantId,
        @NotBlank String sourceType,
        @NotBlank String sourceId,
        @NotNull OffsetDateTime eventTime,
        @NotNull Map<String, Object> payload,
        @NotBlank String traceId
) {
}
