package cn.javahome.frank.cdp.flink.job.service;

import java.util.Map;

/**
 * 简化服务层：封装当前模块核心业务入口。
 */
public interface FlinkJobService {

    Map<String, Object> summary(String tenantId, String traceId);
}
