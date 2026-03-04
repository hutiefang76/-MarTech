package cn.javahome.frank.cdp.api.model;

import java.util.List;
import java.util.Map;

public record PreviewResponse(
        List<String> columns,
        List<Map<String, Object>> rows
) {
}
