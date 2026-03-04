package cn.javahome.frank.cdp.callchain.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/demo")
public class CallChainDemoController {

    @GetMapping("/chain")
    public Map<String, Object> chain() {
        return Map.of(
                "chain", List.of(
                        "cdp-connector-control-service",
                        "cdp-connector-*-service",
                        "cdp-flink-job-service",
                        "cdp-tag-task-service",
                        "cdp-openapi-service"
                ),
                "desc", "business-domain chain showcase"
        );
    }

    @GetMapping("/flink-tag-chain")
    public Map<String, Object> flinkTagChain() {
        return Map.of(
                "chain", List.of(
                        "cdp-tag-service",
                        "cdp-tag-task-service",
                        "cdp-flink-job-service"
                ),
                "desc", "tag task publish and deploy flink-on-k8s"
        );
    }

    @GetMapping("/import-chain")
    public Map<String, Object> importChain() {
        return Map.of(
                "chain", List.of(
                        "cdp-connector-control-service",
                        "cdp-flink-job-service"
                ),
                "desc", "data import deploy directly to flink-on-k8s"
        );
    }
}
