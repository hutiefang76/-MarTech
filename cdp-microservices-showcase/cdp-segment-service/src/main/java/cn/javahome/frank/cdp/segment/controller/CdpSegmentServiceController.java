package cn.javahome.frank.cdp.segment.controller;

import cn.javahome.frank.cdp.common.TraceHeader;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/segment")
public class CdpSegmentServiceController {

    @GetMapping("/ping")
    public Map<String, Object> ping(@RequestParam(defaultValue = "t001") String tenantId,
                                    @RequestParam(defaultValue = "trace-demo") String traceId) {
        TraceHeader header = new TraceHeader(tenantId, traceId);
        return Map.of(
                "service", "cdp-segment-service",
                "tenantId", header.tenantId(),
                "traceId", header.traceId(),
                "status", "ok"
        );
    }
}
