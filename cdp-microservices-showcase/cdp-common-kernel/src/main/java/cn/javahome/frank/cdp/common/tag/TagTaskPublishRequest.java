package cn.javahome.frank.cdp.common.tag;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TagTaskPublishRequest(
        @NotBlank String tenantId,
        @NotBlank String traceId,
        @NotBlank String tagCode,
        @NotBlank String taskName,
        @NotNull TagTaskExecutionMode executionMode,
        String flinkSql,
        String dorisSql
) {
}
