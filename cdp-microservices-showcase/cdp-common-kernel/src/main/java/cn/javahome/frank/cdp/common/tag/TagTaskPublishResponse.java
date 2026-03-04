package cn.javahome.frank.cdp.common.tag;

public record TagTaskPublishResponse(
        String taskId,
        String tagCode,
        TagTaskExecutionMode executionMode,
        String engineTaskId,
        TagTaskStatus status,
        String message
) {
}
