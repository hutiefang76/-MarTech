package cn.javahome.frank.cdp.audience.service;

import java.util.Map;

/**
 * 简化服务层：封装当前模块核心业务入口。
 */
public interface AudienceService {

    Map<String, Object> summary(String tenantId, String traceId);
}
