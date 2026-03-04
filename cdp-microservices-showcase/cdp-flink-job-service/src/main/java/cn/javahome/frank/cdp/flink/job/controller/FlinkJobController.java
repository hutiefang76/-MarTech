package cn.javahome.frank.cdp.flink.job.controller;

import cn.javahome.frank.cdp.common.flink.FlinkJobStatusResponse;
import cn.javahome.frank.cdp.common.flink.FlinkJobSubmitRequest;
import cn.javahome.frank.cdp.common.flink.FlinkJobSubmitResponse;
import cn.javahome.frank.cdp.flink.job.service.FlinkJobRegistry;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/flink/jobs")
public class FlinkJobController {

    private final FlinkJobRegistry registry;

    public FlinkJobController(FlinkJobRegistry registry) {
        this.registry = registry;
    }

    @PostMapping("/deploy-k8s")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public FlinkJobSubmitResponse deployK8s(@Valid @RequestBody FlinkJobSubmitRequest request) {
        return registry.deployOnK8s(request);
    }

    @GetMapping("/{jobId}")
    public FlinkJobStatusResponse jobStatus(@PathVariable String jobId) {
        FlinkJobStatusResponse response = registry.getByJobId(jobId);
        if (response == null) {
            throw new IllegalArgumentException("job not found: " + jobId);
        }
        return response;
    }

    @GetMapping("/by-biz/{bizTaskId}")
    public FlinkJobStatusResponse byBizTaskId(@PathVariable String bizTaskId) {
        FlinkJobStatusResponse response = registry.getByBizTaskId(bizTaskId);
        if (response == null) {
            throw new IllegalArgumentException("job not found for bizTaskId: " + bizTaskId);
        }
        return response;
    }

    @PostMapping("/{jobId}/mock/mark")
    public FlinkJobStatusResponse mark(@PathVariable String jobId,
                                       @RequestParam String status,
                                       @RequestParam(defaultValue = "manually updated") String detail) {
        FlinkJobStatusResponse response = registry.mark(jobId, status, detail);
        if (response == null) {
            throw new IllegalArgumentException("job not found: " + jobId);
        }
        return response;
    }

    @GetMapping("/ods/summary")
    public Map<String, Object> odsSummary() {
        return registry.odsSummary();
    }

    @GetMapping("/ods/latest")
    public List<Map<String, Object>> latestOds(@RequestParam(required = false) String sourceType,
                                               @RequestParam(defaultValue = "20") int limit) {
        return registry.latestFromOds(sourceType, limit);
    }

    @GetMapping("/ods/job/{jobId}")
    public List<Map<String, Object>> jobOds(@PathVariable String jobId,
                                            @RequestParam(defaultValue = "50") int limit) {
        return registry.byJobIdFromOds(jobId, limit);
    }
}
