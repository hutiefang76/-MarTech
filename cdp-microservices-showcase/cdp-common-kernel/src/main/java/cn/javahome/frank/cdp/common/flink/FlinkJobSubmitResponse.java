package cn.javahome.frank.cdp.common.flink;

public record FlinkJobSubmitResponse(
        String jobId,
        String deploymentId,
        String status,
        String message
) {
}
