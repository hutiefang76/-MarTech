package cn.javahome.frank.cdp.flink.job.service;

import cn.javahome.frank.cdp.common.flink.FlinkJobStatusResponse;
import cn.javahome.frank.cdp.common.flink.FlinkJobSubmitRequest;
import cn.javahome.frank.cdp.common.flink.FlinkJobSubmitResponse;
import cn.javahome.frank.cdp.common.flink.FlinkJobType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class FlinkJobRegistry {

    private static final int MAX_MEMORY_EVENTS = 5000;

    private final Map<String, FlinkJobStatusResponse> byJobId = new ConcurrentHashMap<>();
    private final Map<String, String> jobIdByBizTaskId = new ConcurrentHashMap<>();
    private final Deque<OdsEvent> memoryEvents = new ConcurrentLinkedDeque<>();
    private final Map<String, AtomicInteger> mysqlRowsByJobId = new ConcurrentHashMap<>();
    private final ExecutorService simulatorExecutor = Executors.newCachedThreadPool();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final JdbcTemplate jdbcTemplate;
    private volatile boolean mysqlTableReady = false;

    public FlinkJobRegistry(ObjectProvider<JdbcTemplate> jdbcTemplateProvider) {
        this.jdbcTemplate = jdbcTemplateProvider.getIfAvailable();
    }

    @PostConstruct
    public void initOdsTable() {
        if (jdbcTemplate == null) {
            mysqlTableReady = false;
            return;
        }
        try {
            jdbcTemplate.execute("""
                    CREATE TABLE IF NOT EXISTS cdp_ods_simulated_event (
                        id BIGINT PRIMARY KEY AUTO_INCREMENT,
                        job_id VARCHAR(128) NOT NULL,
                        source_type VARCHAR(64) NOT NULL,
                        target_table_name VARCHAR(128) NOT NULL,
                        data_key VARCHAR(128) NOT NULL,
                        payload_json TEXT NOT NULL,
                        event_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
                    )
                    """);
            mysqlTableReady = true;
        } catch (Exception ex) {
            mysqlTableReady = false;
        }
    }

    public FlinkJobSubmitResponse deployOnK8s(FlinkJobSubmitRequest request) {
        String jobId = "flink-job-" + UUID.randomUUID();
        String deploymentId = "k8s-app-" + UUID.randomUUID();
        FlinkJobStatusResponse status = new FlinkJobStatusResponse(
                jobId,
                deploymentId,
                request.bizTaskId(),
                "RUNNING",
                "submitted to flink-on-k8s"
        );
        byJobId.put(jobId, status);
        jobIdByBizTaskId.put(request.bizTaskId(), jobId);

        CompletableFuture.runAsync(() -> simulateOdsStreaming(jobId, request), simulatorExecutor);
        return new FlinkJobSubmitResponse(jobId, deploymentId, "RUNNING", "deployment triggered");
    }

    public FlinkJobStatusResponse getByJobId(String jobId) {
        return byJobId.get(jobId);
    }

    public FlinkJobStatusResponse getByBizTaskId(String bizTaskId) {
        String jobId = jobIdByBizTaskId.get(bizTaskId);
        return jobId == null ? null : byJobId.get(jobId);
    }

    public FlinkJobStatusResponse mark(String jobId, String status, String detail) {
        FlinkJobStatusResponse old = byJobId.get(jobId);
        if (old == null) {
            return null;
        }
        FlinkJobStatusResponse updated = new FlinkJobStatusResponse(
                old.jobId(),
                old.deploymentId(),
                old.bizTaskId(),
                status,
                detail
        );
        byJobId.put(jobId, updated);
        return updated;
    }

    public List<Map<String, Object>> latestFromOds(String sourceType, int limit) {
        int safeLimit = normalizeLimit(limit);
        if (mysqlTableReady && jdbcTemplate != null) {
            try {
                if (sourceType == null || sourceType.isBlank()) {
                    return jdbcTemplate.queryForList("""
                            SELECT job_id, source_type, target_table_name, data_key, payload_json, event_time
                            FROM cdp_ods_simulated_event
                            ORDER BY id DESC
                            LIMIT ?
                            """, safeLimit);
                }
                return jdbcTemplate.queryForList("""
                        SELECT job_id, source_type, target_table_name, data_key, payload_json, event_time
                        FROM cdp_ods_simulated_event
                        WHERE source_type = ?
                        ORDER BY id DESC
                        LIMIT ?
                        """, sourceType.toUpperCase(), safeLimit);
            } catch (Exception ex) {
                mysqlTableReady = false;
            }
        }
        return fromMemory(sourceType, null, safeLimit);
    }

    public List<Map<String, Object>> byJobIdFromOds(String jobId, int limit) {
        int safeLimit = normalizeLimit(limit);
        if (mysqlTableReady && jdbcTemplate != null) {
            try {
                return jdbcTemplate.queryForList("""
                        SELECT job_id, source_type, target_table_name, data_key, payload_json, event_time
                        FROM cdp_ods_simulated_event
                        WHERE job_id = ?
                        ORDER BY id DESC
                        LIMIT ?
                        """, jobId, safeLimit);
            } catch (Exception ex) {
                mysqlTableReady = false;
            }
        }
        return fromMemory(null, jobId, safeLimit);
    }

    public Map<String, Object> odsSummary() {
        Map<String, Long> memoryBySource = memoryEvents.stream()
                .collect(Collectors.groupingBy(OdsEvent::sourceType, Collectors.counting()));

        int mysqlTotal = -1;
        if (mysqlTableReady && jdbcTemplate != null) {
            try {
                Integer value = jdbcTemplate.queryForObject("SELECT COUNT(1) FROM cdp_ods_simulated_event", Integer.class);
                mysqlTotal = value == null ? 0 : value;
            } catch (Exception ex) {
                mysqlTableReady = false;
                mysqlTotal = -1;
            }
        }

        Map<String, Integer> jobRows = mysqlRowsByJobId.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().get()));

        return Map.of(
                "mysqlReady", mysqlTableReady,
                "mysqlTotalRows", mysqlTotal,
                "memoryTotalRows", memoryEvents.size(),
                "memoryRowsBySource", memoryBySource,
                "mysqlRowsByJob", jobRows,
                "jobTotal", byJobId.size()
        );
    }

    private void simulateOdsStreaming(String jobId, FlinkJobSubmitRequest request) {
        try {
            int totalRows = expectedRows(request.jobType());
            boolean realtime = isRealtimeSource(request.jobType());
            for (int i = 1; i <= totalRows; i++) {
                Map<String, Object> payload = buildPayload(request, i);
                appendOdsEvent(jobId, request, payload, i);
                if (realtime) {
                    Thread.sleep(120L);
                }
            }
            mark(jobId, "SUCCESS", "simulated ingestion completed, rows=" + totalRows);
        } catch (Exception ex) {
            mark(jobId, "FAILED", "simulation failed: " + ex.getMessage());
        }
    }

    private int expectedRows(FlinkJobType jobType) {
        return switch (jobType) {
            case IMPORT_MYSQL_TO_DORIS -> 30;
            case IMPORT_KAFKA_TO_DORIS -> 36;
            case IMPORT_ES_TO_DORIS -> 24;
            case IMPORT_FILE_TO_DORIS -> 15;
            case TAG_REALTIME -> 12;
        };
    }

    private boolean isRealtimeSource(FlinkJobType jobType) {
        return switch (jobType) {
            case IMPORT_FILE_TO_DORIS -> false;
            default -> true;
        };
    }

    private void appendOdsEvent(String jobId, FlinkJobSubmitRequest request, Map<String, Object> payload, int seq) {
        String sourceType = sourceTypeByJobType(request.jobType());
        String targetTable = resolveTargetTable(request, sourceType);
        String dataKey = jobId + "-" + seq;
        String payloadJson = toJson(payload);
        OffsetDateTime now = OffsetDateTime.now();

        OdsEvent event = new OdsEvent(jobId, sourceType, targetTable, dataKey, payloadJson, now);
        memoryEvents.addFirst(event);
        while (memoryEvents.size() > MAX_MEMORY_EVENTS) {
            memoryEvents.pollLast();
        }

        if (mysqlTableReady && jdbcTemplate != null) {
            try {
                jdbcTemplate.update("""
                                INSERT INTO cdp_ods_simulated_event
                                (job_id, source_type, target_table_name, data_key, payload_json, event_time)
                                VALUES (?, ?, ?, ?, ?, ?)
                                """,
                        jobId,
                        sourceType,
                        targetTable,
                        dataKey,
                        payloadJson,
                        now.toLocalDateTime()
                );
                mysqlRowsByJobId.computeIfAbsent(jobId, k -> new AtomicInteger(0)).incrementAndGet();
            } catch (Exception ex) {
                mysqlTableReady = false;
            }
        }
    }

    private String resolveTargetTable(FlinkJobSubmitRequest request, String sourceType) {
        if (request.params() != null && request.params().containsKey("targetTable")) {
            return request.params().get("targetTable");
        }
        return "ods_" + sourceType.toLowerCase() + "_simulated";
    }

    private String sourceTypeByJobType(FlinkJobType jobType) {
        return switch (jobType) {
            case IMPORT_MYSQL_TO_DORIS -> "MYSQL";
            case IMPORT_KAFKA_TO_DORIS -> "KAFKA";
            case IMPORT_ES_TO_DORIS -> "ES";
            case IMPORT_FILE_TO_DORIS -> "FILE";
            case TAG_REALTIME -> "TAG";
        };
    }

    private Map<String, Object> buildPayload(FlinkJobSubmitRequest request, int seq) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("tenantId", request.tenantId());
        payload.put("traceId", request.traceId());
        payload.put("bizTaskId", request.bizTaskId());
        payload.put("jobType", request.jobType().name());
        payload.put("seq", seq);
        payload.put("eventTs", OffsetDateTime.now().toString());

        switch (request.jobType()) {
            case IMPORT_MYSQL_TO_DORIS -> {
                payload.put("table", "crm_user");
                payload.put("op", seq % 3 == 0 ? "UPDATE" : "INSERT");
                payload.put("userId", 100000 + seq);
            }
            case IMPORT_KAFKA_TO_DORIS -> {
                payload.put("topic", "ods_user_behavior");
                payload.put("event", seq % 2 == 0 ? "VIEW" : "CLICK");
                payload.put("deviceId", "dev-" + (1000 + seq));
            }
            case IMPORT_ES_TO_DORIS -> {
                payload.put("index", "behavior_index");
                payload.put("docId", "es-" + seq);
                payload.put("score", seq % 10);
            }
            case IMPORT_FILE_TO_DORIS -> {
                payload.put("fileName", "batch_" + (seq % 3 + 1) + ".csv");
                payload.put("lineNo", seq);
                payload.put("schedule", "T+1");
            }
            case TAG_REALTIME -> {
                payload.put("tagCode", request.params() == null ? "unknown" : request.params().getOrDefault("tagCode", "unknown"));
                payload.put("tagValue", seq % 2 == 0 ? "Y" : "N");
            }
        }
        return payload;
    }

    private List<Map<String, Object>> fromMemory(String sourceType, String jobId, int limit) {
        String source = sourceType == null ? null : sourceType.toUpperCase();
        return memoryEvents.stream()
                .filter(event -> source == null || Objects.equals(source, event.sourceType()))
                .filter(event -> jobId == null || Objects.equals(jobId, event.jobId()))
                .sorted(Comparator.comparing(OdsEvent::eventTime).reversed())
                .limit(limit)
                .map(event -> Map.<String, Object>of(
                        "job_id", event.jobId(),
                        "source_type", event.sourceType(),
                        "target_table_name", event.targetTableName(),
                        "data_key", event.dataKey(),
                        "payload_json", event.payloadJson(),
                        "event_time", event.eventTime().toString(),
                        "storage", "MEMORY"
                ))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private String toJson(Map<String, Object> payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            return payload.toString();
        }
    }

    private int normalizeLimit(int limit) {
        if (limit <= 0) {
            return 20;
        }
        return Math.min(limit, 200);
    }

    private record OdsEvent(
            String jobId,
            String sourceType,
            String targetTableName,
            String dataKey,
            String payloadJson,
            OffsetDateTime eventTime
    ) {
    }
}
