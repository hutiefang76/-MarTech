package cn.javahome.frank.cdp.api.model;

import java.util.List;

public record SchemaResponse(
        String objectName,
        List<SchemaField> fields
) {
}
