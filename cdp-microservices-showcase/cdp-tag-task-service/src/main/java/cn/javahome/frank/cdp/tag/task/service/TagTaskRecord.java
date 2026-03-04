package cn.javahome.frank.cdp.tag.task.service;

import cn.javahome.frank.cdp.common.tag.TagTaskExecutionMode;
import cn.javahome.frank.cdp.common.tag.TagTaskStatus;

import java.time.OffsetDateTime;

public record TagTaskRecord(
        String taskId,
        String tenantId,
        String traceId,
        String tagCode,
        String taskName,
        TagTaskExecutionMode mode,
        String engineTaskId,
        TagTaskStatus status,
        String message,
        OffsetDateTime updatedAt
) {
}
