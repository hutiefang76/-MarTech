package cn.javahome.frank.cdp.connector.fs.entity;

import java.time.OffsetDateTime;

/**
 * 简化实体：用于演示每个微服务都具备实体层。
 */
public record ConnectorFsEntity(
        String id,
        String tenantId,
        String traceId,
        OffsetDateTime updatedAt
) {
}
