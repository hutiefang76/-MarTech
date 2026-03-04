package cn.javahome.frank.cdp.activation.controller;

import cn.javahome.frank.cdp.activation.service.ActivationService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 简化控制器：统一入口，调用 service。
 */
@RestController
@RequestMapping("/activation")
public class ActivationEntryController {

    private final ActivationService service;

    public ActivationEntryController(ActivationService service) {
        this.service = service;
    }

    @GetMapping("/entry")
    public Map<String, Object> entry(@RequestParam(defaultValue = "t001") String tenantId,
                                     @RequestParam(defaultValue = "trace-entry") String traceId) {
        return service.summary(tenantId, traceId);
    }
}
