package cn.javahome.frank.cdp.api.controller;

import cn.javahome.frank.cdp.api.model.BaseResponse;
import cn.javahome.frank.cdp.api.model.StandardizedRecord;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Validated
@RestController
@RequestMapping("/internal/v1/ingestion")
public class InternalIngestionController {

    @PostMapping("/records")
    public BaseResponse ingestRecords(@Valid @RequestBody RecordsRequest request) {
        return BaseResponse.success();
    }

    @PostMapping("/schema/register")
    public BaseResponse registerSchema(@Valid @RequestBody RegisterSchemaRequest request) {
        return BaseResponse.success();
    }

    @GetMapping("/topics")
    public Map<String, Object> listTopics() {
        return Map.of(
                "data",
                List.of(
                        Map.of("sourceId", "crm_mysql_user", "odsTopic", "cdp_ods_crm_user"),
                        Map.of("sourceId", "es_behavior", "odsTopic", "cdp_ods_behavior_es")
                )
        );
    }

    public record RecordsRequest(
            @NotNull List<@Valid StandardizedRecord> records
    ) {
    }

    public record RegisterSchemaRequest(
            @NotBlank String tenantId,
            @NotBlank String sourceId,
            @NotBlank String schemaJson
    ) {
    }
}
