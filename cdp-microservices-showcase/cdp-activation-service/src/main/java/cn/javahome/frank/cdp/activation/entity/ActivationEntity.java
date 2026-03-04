package cn.javahome.frank.cdp.activation.entity;

import java.time.OffsetDateTime;

/**
 * 简化实体：用于演示每个微服务都具备实体层。
 */
public record ActivationEntity(
        String id,
        String tenantId,
        String traceId,
        OffsetDateTime updatedAt
) {
}
