package cn.javahome.frank.cdp.api.controller;

import cn.javahome.frank.cdp.api.model.ConnectorResponse;
import cn.javahome.frank.cdp.api.model.ConnectorType;
import cn.javahome.frank.cdp.api.model.ConnectorUpsertRequest;
import cn.javahome.frank.cdp.api.model.PagedConnectorResponse;
import cn.javahome.frank.cdp.api.model.PagedTaskResponse;
import cn.javahome.frank.cdp.api.model.PreviewResponse;
import cn.javahome.frank.cdp.api.model.PublishRequest;
import cn.javahome.frank.cdp.api.model.PublishResponse;
import cn.javahome.frank.cdp.api.model.SchemaField;
import cn.javahome.frank.cdp.api.model.SchemaResponse;
import cn.javahome.frank.cdp.api.model.TaskMode;
import cn.javahome.frank.cdp.api.model.TaskResponse;
import cn.javahome.frank.cdp.api.model.TaskStatus;
import cn.javahome.frank.cdp.api.model.TestResultResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Validated
@RestController
@RequestMapping("/api/v1/connectors")
public class ConnectorController {

    @PostMapping
    public ResponseEntity<ConnectorResponse> createConnector(@Valid @RequestBody ConnectorUpsertRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mockConnector(request));
    }

    @GetMapping
    public PagedConnectorResponse listConnectors(@RequestParam(defaultValue = "1") int pageNo,
                                                 @RequestParam(defaultValue = "20") int pageSize,
                                                 @RequestParam(required = false) ConnectorType type,
                                                 @RequestParam(required = false) String keyword) {
        return new PagedConnectorResponse(pageNo, pageSize, 1, List.of(mockConnector(null)));
    }

    @GetMapping("/{connectorId}")
    public ConnectorResponse getConnector(@PathVariable String connectorId) {
        ConnectorResponse response = mockConnector(null);
        return new ConnectorResponse(
                connectorId,
                response.name(),
                response.type(),
                response.tenantId(),
                response.status(),
                response.config(),
                response.createdAt(),
                response.updatedAt()
        );
    }

    @PutMapping("/{connectorId}")
    public ConnectorResponse updateConnector(@PathVariable String connectorId,
                                             @Valid @RequestBody ConnectorUpsertRequest request) {
        ConnectorResponse response = mockConnector(request);
        return new ConnectorResponse(
                connectorId,
                response.name(),
                response.type(),
                response.tenantId(),
                response.status(),
                response.config(),
                response.createdAt(),
                response.updatedAt()
        );
    }

    @PostMapping("/{connectorId}/test")
    public TestResultResponse testConnector(@PathVariable String connectorId) {
        return new TestResultResponse(true, 38, "connection ok");
    }

    @GetMapping("/{connectorId}/schema")
    public SchemaResponse getSchema(@PathVariable String connectorId) {
        return new SchemaResponse(
                "demo_user_table",
                List.of(
                        new SchemaField("user_id", "BIGINT", false, "user id"),
                        new SchemaField("mobile", "VARCHAR(32)", true, "mobile"),
                        new SchemaField("updated_at", "DATETIME", false, "update time")
                )
        );
    }

    @GetMapping("/{connectorId}/preview")
    public PreviewResponse preview(@PathVariable String connectorId,
                                   @RequestParam(defaultValue = "20") int limit) {
        return new PreviewResponse(
                List.of("user_id", "mobile", "updated_at"),
                List.of(
                        Map.of("user_id", 10001, "mobile", "13800000001", "updated_at", "2026-03-04 10:10:00"),
                        Map.of("user_id", 10002, "mobile", "13800000002", "updated_at", "2026-03-04 10:12:00")
                )
        );
    }

    @PostMapping("/{connectorId}/publish")
    public PublishResponse publish(@PathVariable String connectorId,
                                   @RequestBody(required = false) PublishRequest request) {
        return new PublishResponse(
                "task-" + UUID.randomUUID(),
                "wf-" + UUID.randomUUID(),
                "ins-" + UUID.randomUUID(),
                TaskStatus.PUBLISHED
        );
    }

    @GetMapping("/{connectorId}/tasks")
    public PagedTaskResponse listConnectorTasks(@PathVariable String connectorId,
                                                @RequestParam(defaultValue = "1") int pageNo,
                                                @RequestParam(defaultValue = "20") int pageSize) {
        TaskResponse task = new TaskResponse(
                "task-" + UUID.randomUUID(),
                connectorId,
                TaskMode.REALTIME,
                TaskStatus.RUNNING,
                "wf-demo-01",
                "ins-demo-01",
                OffsetDateTime.now().minusMinutes(3),
                null,
                null
        );
        return new PagedTaskResponse(pageNo, pageSize, 1, List.of(task));
    }

    private ConnectorResponse mockConnector(ConnectorUpsertRequest request) {
        ConnectorType type = request == null || request.type() == null ? ConnectorType.MYSQL_CDC : request.type();
        String name = request == null ? "demo-mysql-cdc" : request.name();
        String tenantId = request == null ? "t001" : request.tenantId();
        Map<String, Object> config = request == null ? Map.of("host", "127.0.0.1", "port", 3306) : request.config();
        return new ConnectorResponse(
                "conn-" + UUID.randomUUID(),
                name,
                type,
                tenantId,
                "ACTIVE",
                config,
                OffsetDateTime.now(),
                OffsetDateTime.now()
        );
    }
}
