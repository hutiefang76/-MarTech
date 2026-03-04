package cn.javahome.frank.cdp.tag.controller;

import cn.javahome.frank.cdp.common.TraceHeader;
import cn.javahome.frank.cdp.common.tag.TagTaskExecutionMode;
import cn.javahome.frank.cdp.common.tag.TagTaskPublishRequest;
import cn.javahome.frank.cdp.common.tag.TagTaskPublishResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Validated
@RestController
@RequestMapping("/tag")
public class TagServiceController {

    private final RestClient tagTaskRestClient;

    public TagServiceController(RestClient tagTaskRestClient) {
        this.tagTaskRestClient = tagTaskRestClient;
    }

    @GetMapping("/ping")
    public Map<String, Object> ping(@RequestParam(defaultValue = "t001") String tenantId,
                                    @RequestParam(defaultValue = "trace-demo") String traceId) {
        TraceHeader header = new TraceHeader(tenantId, traceId);
        return Map.of(
                "service", "cdp-tag-service",
                "tenantId", header.tenantId(),
                "traceId", header.traceId(),
                "status", "ok"
        );
    }

    @PostMapping("/{tagCode}/publish-realtime")
    public TagTaskPublishResponse publishRealtime(@PathVariable @NotBlank String tagCode,
                                                  @Valid @RequestBody PublishRealtimeRequest request) {
        TagTaskPublishRequest publishRequest = new TagTaskPublishRequest(
                request.tenantId(),
                request.traceId(),
                tagCode,
                request.taskName(),
                TagTaskExecutionMode.FLINK_REALTIME,
                request.flinkSql(),
                null
        );
        return tagTaskRestClient.post()
                .uri("/tag/task/publish")
                .body(publishRequest)
                .retrieve()
                .body(TagTaskPublishResponse.class);
    }

    @GetMapping("/tasks/{taskId}")
    public TagTaskPublishResponse getTask(@PathVariable String taskId) {
        return tagTaskRestClient.get()
                .uri("/tag/task/{taskId}", taskId)
                .retrieve()
                .body(TagTaskPublishResponse.class);
    }

    public record PublishRealtimeRequest(
            @NotBlank String tenantId,
            @NotBlank String traceId,
            @NotBlank String taskName,
            @NotBlank String flinkSql
    ) {
    }
}
