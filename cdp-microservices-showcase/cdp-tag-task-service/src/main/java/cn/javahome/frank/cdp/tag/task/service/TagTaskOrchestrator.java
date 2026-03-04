package cn.javahome.frank.cdp.tag.task.service;

import cn.javahome.frank.cdp.common.flink.FlinkDeployMode;
import cn.javahome.frank.cdp.common.flink.FlinkJobStatusResponse;
import cn.javahome.frank.cdp.common.flink.FlinkJobSubmitRequest;
import cn.javahome.frank.cdp.common.flink.FlinkJobSubmitResponse;
import cn.javahome.frank.cdp.common.flink.FlinkJobType;
import cn.javahome.frank.cdp.common.tag.TagTaskExecutionMode;
import cn.javahome.frank.cdp.common.tag.TagTaskPublishRequest;
import cn.javahome.frank.cdp.common.tag.TagTaskPublishResponse;
import cn.javahome.frank.cdp.common.tag.TagTaskStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TagTaskOrchestrator {

    private final RestClient flinkRestClient;
    private final Map<String, TagTaskRecord> records = new ConcurrentHashMap<>();

    public TagTaskOrchestrator(RestClient flinkRestClient) {
        this.flinkRestClient = flinkRestClient;
    }

    public TagTaskPublishResponse publish(TagTaskPublishRequest request) {
        String taskId = "tag-task-" + UUID.randomUUID();
        if (request.executionMode() == TagTaskExecutionMode.FLINK_REALTIME) {
            FlinkJobSubmitRequest flinkReq = new FlinkJobSubmitRequest(
                    request.tenantId(),
                    request.traceId(),
                    taskId,
                    FlinkJobType.TAG_REALTIME,
                    FlinkDeployMode.K8S_APPLICATION,
                    "cn.javahome.frank.cdp.flink.job.example.MySqlToDorisJob",
                    "hdfs:///cdp/jobs/tag-realtime-job.jar",
                    request.flinkSql(),
                    Map.of("tagCode", request.tagCode())
            );

            FlinkJobSubmitResponse flinkResp = flinkRestClient.post()
                    .uri("/flink/jobs/deploy-k8s")
                    .body(flinkReq)
                    .retrieve()
                    .body(FlinkJobSubmitResponse.class);

            String engineTaskId = flinkResp == null ? null : flinkResp.jobId();
            TagTaskStatus status = flinkResp == null ? TagTaskStatus.FAILED : TagTaskStatus.RUNNING;
            String message = flinkResp == null ? "flink deploy failed" : flinkResp.message();
            TagTaskRecord record = new TagTaskRecord(
                    taskId,
                    request.tenantId(),
                    request.traceId(),
                    request.tagCode(),
                    request.taskName(),
                    request.executionMode(),
                    engineTaskId,
                    status,
                    message,
                    OffsetDateTime.now()
            );
            records.put(taskId, record);
            return toResponse(record);
        }

        String batchEngineId = "dolphin-task-" + UUID.randomUUID();
        TagTaskRecord record = new TagTaskRecord(
                taskId,
                request.tenantId(),
                request.traceId(),
                request.tagCode(),
                request.taskName(),
                request.executionMode(),
                batchEngineId,
                TagTaskStatus.SUBMITTED,
                "submitted to doris batch pipeline",
                OffsetDateTime.now()
        );
        records.put(taskId, record);
        return toResponse(record);
    }

    public TagTaskPublishResponse get(String taskId) {
        TagTaskRecord old = records.get(taskId);
        if (old == null) {
            throw new IllegalArgumentException("task not found: " + taskId);
        }

        if (old.mode() == TagTaskExecutionMode.FLINK_REALTIME && old.engineTaskId() != null) {
            FlinkJobStatusResponse status = flinkRestClient.get()
                    .uri("/flink/jobs/{jobId}", old.engineTaskId())
                    .retrieve()
                    .body(FlinkJobStatusResponse.class);
            if (status != null) {
                TagTaskStatus mapped = mapStatus(status.status());
                TagTaskRecord updated = new TagTaskRecord(
                        old.taskId(),
                        old.tenantId(),
                        old.traceId(),
                        old.tagCode(),
                        old.taskName(),
                        old.mode(),
                        old.engineTaskId(),
                        mapped,
                        status.detail(),
                        OffsetDateTime.now()
                );
                records.put(taskId, updated);
                return toResponse(updated);
            }
        }
        return toResponse(old);
    }

    public List<TagTaskPublishResponse> list() {
        return records.values().stream().map(this::toResponse).toList();
    }

    private TagTaskPublishResponse toResponse(TagTaskRecord record) {
        return new TagTaskPublishResponse(
                record.taskId(),
                record.tagCode(),
                record.mode(),
                record.engineTaskId(),
                record.status(),
                record.message()
        );
    }

    private TagTaskStatus mapStatus(String status) {
        return switch (status) {
            case "RUNNING" -> TagTaskStatus.RUNNING;
            case "SUCCESS" -> TagTaskStatus.SUCCESS;
            case "FAILED" -> TagTaskStatus.FAILED;
            default -> TagTaskStatus.SUBMITTED;
        };
    }
}
