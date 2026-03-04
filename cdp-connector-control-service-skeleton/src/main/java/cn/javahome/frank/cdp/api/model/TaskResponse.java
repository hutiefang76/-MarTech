package cn.javahome.frank.cdp.api.model;

import java.time.OffsetDateTime;

public record TaskResponse(
        String taskId,
        String connectorId,
        TaskMode mode,
        TaskStatus status,
        String workflowCode,
        String instanceId,
        OffsetDateTime startTime,
        OffsetDateTime endTime,
        String errorMessage
) {
}
