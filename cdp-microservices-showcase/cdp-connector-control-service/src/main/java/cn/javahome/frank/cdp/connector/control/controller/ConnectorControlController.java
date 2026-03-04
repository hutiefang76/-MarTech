package cn.javahome.frank.cdp.connector.control.controller;

import cn.javahome.frank.cdp.common.TraceHeader;
import cn.javahome.frank.cdp.common.flink.FlinkDeployMode;
import cn.javahome.frank.cdp.common.flink.FlinkJobSubmitRequest;
import cn.javahome.frank.cdp.common.flink.FlinkJobSubmitResponse;
import cn.javahome.frank.cdp.common.flink.FlinkJobType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import java.util.Map;
import java.util.UUID;

@Validated
@RestController
@RequestMapping("/connector/control")
public class ConnectorControlController {

    private final RestClient flinkRestClient;

    public ConnectorControlController(RestClient flinkRestClient) {
        this.flinkRestClient = flinkRestClient;
    }

    @GetMapping("/ping")
    public Map<String, Object> ping(@RequestParam(defaultValue = "t001") String tenantId,
                                    @RequestParam(defaultValue = "trace-demo") String traceId) {
        TraceHeader header = new TraceHeader(tenantId, traceId);
        return Map.of(
                "service", "cdp-connector-control-service",
                "tenantId", header.tenantId(),
                "traceId", header.traceId(),
                "status", "ok"
        );
    }

    @PostMapping("/import/deploy-k8s")
    public FlinkJobSubmitResponse deployImportJob(@Valid @RequestBody ImportDeployRequest request) {
        FlinkJobType jobType = resolveJobType(request.sourceType());
        String bizTaskId = "import-" + request.connectorId() + "-" + UUID.randomUUID();
        String sql = "INSERT INTO " + request.targetTable() + " SELECT * FROM source_stream";
        FlinkJobSubmitRequest flinkReq = new FlinkJobSubmitRequest(
                request.tenantId(),
                request.traceId(),
                bizTaskId,
                jobType,
                FlinkDeployMode.K8S_APPLICATION,
                "cn.javahome.frank.cdp.flink.job.example.MySqlToDorisJob",
                "hdfs:///cdp/jobs/mysql-to-doris-job.jar",
                sql,
                Map.of(
                        "connectorId", request.connectorId(),
                        "parallelism", String.valueOf(request.parallelism()),
                        "sourceType", request.sourceType(),
                        "targetTable", request.targetTable()
                )
        );

        return flinkRestClient.post()
                .uri("/flink/jobs/deploy-k8s")
                .body(flinkReq)
                .retrieve()
                .body(FlinkJobSubmitResponse.class);
    }

    private FlinkJobType resolveJobType(String sourceType) {
        if (sourceType == null) {
            throw new IllegalArgumentException("sourceType must not be null");
        }
        return switch (sourceType.trim().toUpperCase()) {
            case "MYSQL", "MYSQL_CDC" -> FlinkJobType.IMPORT_MYSQL_TO_DORIS;
            case "KAFKA" -> FlinkJobType.IMPORT_KAFKA_TO_DORIS;
            case "ES", "ELASTICSEARCH" -> FlinkJobType.IMPORT_ES_TO_DORIS;
            case "FILE", "FS", "FILE_SYSTEM" -> FlinkJobType.IMPORT_FILE_TO_DORIS;
            default -> throw new IllegalArgumentException("unsupported sourceType: " + sourceType);
        };
    }

    public record ImportDeployRequest(
            @NotBlank String tenantId,
            @NotBlank String traceId,
            @NotBlank String connectorId,
            @NotBlank String sourceType,
            @NotBlank String targetTable,
            int parallelism
    ) {
    }
}
