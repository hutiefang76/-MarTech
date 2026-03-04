package cn.javahome.frank.cdp.callchain.demo.controller;

import cn.javahome.frank.cdp.common.flink.FlinkJobStatusResponse;
import cn.javahome.frank.cdp.common.flink.FlinkJobSubmitResponse;
import cn.javahome.frank.cdp.common.tag.TagTaskExecutionMode;
import cn.javahome.frank.cdp.common.tag.TagTaskPublishRequest;
import cn.javahome.frank.cdp.common.tag.TagTaskPublishResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Browser-driven demo APIs for simulating CDP microservice call chains.
 */
@RestController
@RequestMapping("/demo/console")
public class DemoConsoleController {

    private final RestClient connectorControlClient;
    private final RestClient tagTaskClient;
    private final RestClient flinkJobClient;

    public DemoConsoleController(@Qualifier("connectorControlClient") RestClient connectorControlClient,
                                 @Qualifier("tagTaskClient") RestClient tagTaskClient,
                                 @Qualifier("flinkJobClient") RestClient flinkJobClient) {
        this.connectorControlClient = connectorControlClient;
        this.tagTaskClient = tagTaskClient;
        this.flinkJobClient = flinkJobClient;
    }

    @GetMapping("/topology")
    public Map<String, Object> topology() {
        return Map.of(
                "topic", "CDP microservice demo call chain",
                "services", List.of(
                        "cdp-callchain-demo",
                        "cdp-connector-control-service",
                        "cdp-tag-task-service",
                        "cdp-flink-job-service",
                        "mysql (ods simulated storage)",
                        "kafka/mysql/es/file (simulated source)"
                ),
                "chains", List.of(
                        "import: callchain -> connector-control -> flink-job",
                        "import-mysql-cdc: trigger flink simulation -> write ODS rows",
                        "import-es: trigger flink simulation -> write ODS rows",
                        "import-kafka: trigger flink simulation -> write ODS rows",
                        "import-file-batch: trigger flink simulation -> write ODS rows",
                        "realtime-tag: callchain -> tag-task -> flink-job",
                        "status: callchain -> flink-job"
                ),
                "observability", Map.of(
                        "skywalking", "http://localhost:18080",
                        "kibana", "http://localhost:5601",
                        "dozzle", "http://localhost:19999"
                )
        );
    }

    @PostMapping("/simulate/import")
    public Map<String, Object> simulateImport(@RequestBody(required = false) ImportCommand command) {
        ImportCommand effective = command == null ? defaultImportCommand() : fillImportDefaults(command);
        String traceId = effective.traceId();

        ImportDeployRequest request = new ImportDeployRequest(
                effective.tenantId(),
                traceId,
                effective.connectorId(),
                effective.sourceType(),
                effective.targetTable(),
                effective.parallelism()
        );

        FlinkJobSubmitResponse response = connectorControlClient.post()
                .uri("/connector/control/import/deploy-k8s")
                .body(request)
                .retrieve()
                .body(FlinkJobSubmitResponse.class);

        return Map.of(
                "traceId", traceId,
                "chain", List.of("cdp-callchain-demo", "cdp-connector-control-service", "cdp-flink-job-service"),
                "request", request,
                "jobSubmit", response,
                "timestamp", OffsetDateTime.now().toString()
        );
    }

    @PostMapping("/simulate/import/mysql-cdc")
    public Map<String, Object> simulateMysqlCdc(@RequestBody(required = false) ImportCommand command) {
        ImportCommand base = command == null ? defaultImportCommand() : fillImportDefaults(command);
        return simulateImport(new ImportCommand(
                base.tenantId(),
                base.traceId(),
                firstNonBlank(base.connectorId(), "mysql-cdc-01"),
                "MYSQL_CDC",
                base.targetTable(),
                base.parallelism()
        ));
    }

    @PostMapping("/simulate/import/kafka")
    public Map<String, Object> simulateKafka(@RequestBody(required = false) ImportCommand command) {
        ImportCommand base = command == null ? defaultImportCommand() : fillImportDefaults(command);
        return simulateImport(new ImportCommand(
                base.tenantId(),
                base.traceId(),
                firstNonBlank(base.connectorId(), "kafka-source-01"),
                "KAFKA",
                base.targetTable(),
                base.parallelism()
        ));
    }

    @PostMapping("/simulate/import/es")
    public Map<String, Object> simulateEs(@RequestBody(required = false) ImportCommand command) {
        ImportCommand base = command == null ? defaultImportCommand() : fillImportDefaults(command);
        return simulateImport(new ImportCommand(
                base.tenantId(),
                base.traceId(),
                firstNonBlank(base.connectorId(), "es-source-01"),
                "ES",
                base.targetTable(),
                base.parallelism()
        ));
    }

    @PostMapping("/simulate/import/file")
    public Map<String, Object> simulateFile(@RequestBody(required = false) ImportCommand command) {
        ImportCommand base = command == null ? defaultImportCommand() : fillImportDefaults(command);
        return simulateImport(new ImportCommand(
                base.tenantId(),
                base.traceId(),
                firstNonBlank(base.connectorId(), "file-batch-01"),
                "FILE",
                base.targetTable(),
                base.parallelism()
        ));
    }

    @PostMapping("/simulate/tag-realtime")
    public Map<String, Object> simulateRealtimeTag(@RequestBody(required = false) TagCommand command) {
        TagCommand effective = command == null ? defaultTagCommand() : fillTagDefaults(command);
        String traceId = effective.traceId();
        String tagCode = effective.tagCode();

        TagTaskPublishRequest request = new TagTaskPublishRequest(
                effective.tenantId(),
                traceId,
                tagCode,
                "rt-task-" + tagCode + "-" + System.currentTimeMillis(),
                TagTaskExecutionMode.FLINK_REALTIME,
                "INSERT INTO cdp_tag_result SELECT * FROM cdp_user_event_stream",
                null
        );

        TagTaskPublishResponse response = tagTaskClient.post()
                .uri("/tag/task/publish")
                .body(request)
                .retrieve()
                .body(TagTaskPublishResponse.class);

        return Map.of(
                "traceId", traceId,
                "chain", List.of("cdp-callchain-demo", "cdp-tag-task-service", "cdp-flink-job-service"),
                "request", request,
                "tagResult", response,
                "timestamp", OffsetDateTime.now().toString()
        );
    }

    @GetMapping("/tag-task/{taskId}")
    public TagTaskPublishResponse queryTagTask(@PathVariable String taskId) {
        return tagTaskClient.get()
                .uri("/tag/task/{taskId}", taskId)
                .retrieve()
                .body(TagTaskPublishResponse.class);
    }

    @GetMapping("/flink/{jobId}")
    public FlinkJobStatusResponse queryFlinkJob(@PathVariable String jobId) {
        return flinkJobClient.get()
                .uri("/flink/jobs/{jobId}", jobId)
                .retrieve()
                .body(FlinkJobStatusResponse.class);
    }

    @GetMapping("/ods/summary")
    public Map<String, Object> odsSummary() {
        return flinkJobClient.get()
                .uri("/flink/jobs/ods/summary")
                .retrieve()
                .body(Map.class);
    }

    @GetMapping("/ods/latest")
    public List<Map<String, Object>> odsLatest(@RequestParam(required = false) String sourceType,
                                               @RequestParam(defaultValue = "20") int limit) {
        return flinkJobClient.get()
                .uri(uriBuilder -> uriBuilder.path("/flink/jobs/ods/latest")
                        .queryParamIfPresent("sourceType", StringUtils.hasText(sourceType) ? java.util.Optional.of(sourceType) : java.util.Optional.empty())
                        .queryParam("limit", limit)
                        .build())
                .retrieve()
                .body(List.class);
    }

    @GetMapping("/ods/job/{jobId}")
    public List<Map<String, Object>> odsByJob(@PathVariable String jobId,
                                              @RequestParam(defaultValue = "50") int limit) {
        return flinkJobClient.get()
                .uri(uriBuilder -> uriBuilder.path("/flink/jobs/ods/job/{jobId}")
                        .queryParam("limit", limit)
                        .build(jobId))
                .retrieve()
                .body(List.class);
    }

    @PostMapping("/flink/{jobId}/mark")
    public FlinkJobStatusResponse markFlinkJob(@PathVariable String jobId,
                                               @RequestParam(defaultValue = "SUCCESS") String status,
                                               @RequestParam(defaultValue = "mock completed by demo console") String detail) {
        return flinkJobClient.post()
                .uri(uriBuilder -> uriBuilder.path("/flink/jobs/{jobId}/mock/mark")
                        .queryParam("status", status)
                        .queryParam("detail", detail)
                        .build(jobId))
                .retrieve()
                .body(FlinkJobStatusResponse.class);
    }

    @PostMapping("/simulate/full-chain")
    public Map<String, Object> simulateFullChain(@RequestBody(required = false) FullChainCommand command) {
        FullChainCommand effective = command == null ? defaultFullChainCommand() : fillFullChainDefaults(command);
        String traceId = effective.traceId();
        List<Map<String, Object>> timeline = new ArrayList<>();

        ImportCommand importCommand = new ImportCommand(
                effective.tenantId(),
                traceId,
                effective.connectorId(),
                effective.sourceType(),
                effective.targetTable(),
                effective.parallelism()
        );
        Map<String, Object> importStep = simulateImport(importCommand);
        timeline.add(Map.of("step", "import", "result", importStep));

        TagCommand tagCommand = new TagCommand(effective.tenantId(), traceId, effective.tagCode());
        Map<String, Object> tagStep = simulateRealtimeTag(tagCommand);
        timeline.add(Map.of("step", "tag-realtime", "result", tagStep));

        return Map.of(
                "traceId", traceId,
                "timeline", timeline,
                "nextAction", "use /demo/console/flink/{jobId}/mark to mock SUCCESS/FAILED",
                "timestamp", OffsetDateTime.now().toString()
        );
    }

    private ImportCommand defaultImportCommand() {
        return new ImportCommand("t001", newTraceId(), "mysql-cdc-01", "MYSQL_CDC", "ods_user_behavior", 2);
    }

    private TagCommand defaultTagCommand() {
        return new TagCommand("t001", newTraceId(), "high_intent_24h");
    }

    private FullChainCommand defaultFullChainCommand() {
        return new FullChainCommand("t001", newTraceId(), "mysql-cdc-01", "MYSQL", "dwd_user_behavior", 2, "high_intent_24h");
    }

    private ImportCommand fillImportDefaults(ImportCommand command) {
        return new ImportCommand(
                firstNonBlank(command.tenantId(), "t001"),
                firstNonBlank(command.traceId(), newTraceId()),
                firstNonBlank(command.connectorId(), "mysql-cdc-01"),
                firstNonBlank(command.sourceType(), "MYSQL_CDC"),
                firstNonBlank(command.targetTable(), "ods_user_behavior"),
                command.parallelism() <= 0 ? 2 : command.parallelism()
        );
    }

    private TagCommand fillTagDefaults(TagCommand command) {
        return new TagCommand(
                firstNonBlank(command.tenantId(), "t001"),
                firstNonBlank(command.traceId(), newTraceId()),
                firstNonBlank(command.tagCode(), "high_intent_24h")
        );
    }

    private FullChainCommand fillFullChainDefaults(FullChainCommand command) {
        return new FullChainCommand(
                firstNonBlank(command.tenantId(), "t001"),
                firstNonBlank(command.traceId(), newTraceId()),
                firstNonBlank(command.connectorId(), "mysql-cdc-01"),
                firstNonBlank(command.sourceType(), "MYSQL"),
                firstNonBlank(command.targetTable(), "dwd_user_behavior"),
                command.parallelism() <= 0 ? 2 : command.parallelism(),
                firstNonBlank(command.tagCode(), "high_intent_24h")
        );
    }

    private String firstNonBlank(String candidate, String fallback) {
        return StringUtils.hasText(candidate) ? candidate : fallback;
    }

    private String newTraceId() {
        return "trace-" + UUID.randomUUID();
    }

    public record ImportCommand(
            String tenantId,
            String traceId,
            String connectorId,
            String sourceType,
            String targetTable,
            int parallelism
    ) {
    }

    public record TagCommand(
            String tenantId,
            String traceId,
            String tagCode
    ) {
    }

    public record FullChainCommand(
            String tenantId,
            String traceId,
            String connectorId,
            String sourceType,
            String targetTable,
            int parallelism,
            String tagCode
    ) {
    }

    public record ImportDeployRequest(
            String tenantId,
            String traceId,
            String connectorId,
            String sourceType,
            String targetTable,
            int parallelism
    ) {
    }
}
