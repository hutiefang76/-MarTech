package cn.javahome.frank.cdp.api.model;

public record TestResultResponse(
        boolean success,
        int latencyMs,
        String message
) {
}

