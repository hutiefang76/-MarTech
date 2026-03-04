package cn.javahome.frank.cdp.api.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Map;

public record ConnectorUpsertRequest(
        @NotBlank String name,
        @NotNull ConnectorType type,
        @NotBlank String tenantId,
        @NotNull Map<String, Object> config,
        @Valid Schedule schedule,
        @Valid Target target
) {
    public record Schedule(
            TaskMode mode,
            String cron,
            @Min(1) Integer parallelism
    ) {
    }

    public record Target(
            String odsTopic
    ) {
    }
}

