package cn.javahome.frank.cdp.common;

import jakarta.validation.constraints.NotBlank;

public record TraceHeader(
        @NotBlank String tenantId,
        @NotBlank String traceId
) {
}
