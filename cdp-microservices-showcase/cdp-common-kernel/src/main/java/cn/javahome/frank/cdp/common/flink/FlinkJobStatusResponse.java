package cn.javahome.frank.cdp.common.flink;

public record FlinkJobStatusResponse(
        String jobId,
        String deploymentId,
        String bizTaskId,
        String status,
        String detail
) {
}
