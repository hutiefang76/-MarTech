package cn.javahome.frank.cdp.api.controller;

import cn.javahome.frank.cdp.api.model.InternalJobRequest;
import cn.javahome.frank.cdp.api.model.InternalJobResponse;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/internal/v1/jobs")
public class InternalConnectorJobController {

    @PostMapping("/mysql-cdc")
    public InternalJobResponse createMysqlCdcJob(@Valid @RequestBody InternalJobRequest request) {
        return accepted("mysql-cdc");
    }

    @PostMapping("/kafka-source")
    public InternalJobResponse createKafkaSourceJob(@Valid @RequestBody InternalJobRequest request) {
        return accepted("kafka-source");
    }

    @PostMapping("/es-source")
    public InternalJobResponse createEsSourceJob(@Valid @RequestBody InternalJobRequest request) {
        return accepted("es-source");
    }

    @PostMapping("/excel-load")
    public InternalJobResponse createExcelLoadJob(@Valid @RequestBody InternalJobRequest request) {
        return accepted("excel-load");
    }

    @PostMapping("/fs-load")
    public InternalJobResponse createFsLoadJob(@Valid @RequestBody InternalJobRequest request) {
        return accepted("fs-load");
    }

    private InternalJobResponse accepted(String type) {
        return new InternalJobResponse(true, type + "-" + UUID.randomUUID(), "accepted");
    }
}
