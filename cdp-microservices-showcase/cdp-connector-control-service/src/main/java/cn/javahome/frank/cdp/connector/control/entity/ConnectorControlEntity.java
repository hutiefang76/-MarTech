package cn.javahome.frank.cdp.connector.control.entity;

import java.time.OffsetDateTime;

/**
 * 简化实体：用于演示每个微服务都具备实体层。
 */
public record ConnectorControlEntity(
        String id,
        String tenantId,
        String traceId,
        OffsetDateTime updatedAt
) {
}
