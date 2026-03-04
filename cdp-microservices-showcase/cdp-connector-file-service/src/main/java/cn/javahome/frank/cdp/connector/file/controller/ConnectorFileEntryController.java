package cn.javahome.frank.cdp.connector.file.controller;

import cn.javahome.frank.cdp.connector.file.service.ConnectorFileService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 简化控制器：统一入口，调用 service。
 */
@RestController
@RequestMapping("/connector/file")
public class ConnectorFileEntryController {

    private final ConnectorFileService service;

    public ConnectorFileEntryController(ConnectorFileService service) {
        this.service = service;
    }

    @GetMapping("/entry")
    public Map<String, Object> entry(@RequestParam(defaultValue = "t001") String tenantId,
                                     @RequestParam(defaultValue = "trace-entry") String traceId) {
        return service.summary(tenantId, traceId);
    }
}
