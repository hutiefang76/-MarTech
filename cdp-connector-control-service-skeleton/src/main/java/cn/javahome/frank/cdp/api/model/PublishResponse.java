package cn.javahome.frank.cdp.api.model;

public record PublishResponse(
        String taskId,
        String workflowCode,
        String instanceId,
        TaskStatus status
) {
}
