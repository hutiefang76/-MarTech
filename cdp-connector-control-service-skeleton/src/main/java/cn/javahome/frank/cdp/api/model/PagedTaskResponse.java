package cn.javahome.frank.cdp.api.model;

import java.util.List;

public record PagedTaskResponse(
        int pageNo,
        int pageSize,
        long total,
        List<TaskResponse> items
) {
}
