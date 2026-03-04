package cn.javahome.frank.cdp.api.model;

import java.time.OffsetDateTime;
import java.util.Map;

public record ConnectorResponse(
        String id,
        String name,
        ConnectorType type,
        String tenantId,
        String status,
        Map<String, Object> config,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}

