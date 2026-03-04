package cn.javahome.frank.cdp.api.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Map;

public record InternalJobRequest(
        @NotBlank String tenantId,
        @NotBlank String connectorId,
        @NotBlank String jobName,
        @NotNull Map<String, Object> config
) {
}
