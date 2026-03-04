package cn.javahome.frank.cdp.common.flink;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Map;

public record FlinkJobSubmitRequest(
        @NotBlank String tenantId,
        @NotBlank String traceId,
        @NotBlank String bizTaskId,
        @NotNull FlinkJobType jobType,
        @NotNull FlinkDeployMode deployMode,
        @NotBlank String jobClass,
        @NotBlank String jarPath,
        String flinkSql,
        Map<String, String> params
) {
}
