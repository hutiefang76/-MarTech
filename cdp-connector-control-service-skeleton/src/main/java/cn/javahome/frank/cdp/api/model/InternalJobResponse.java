package cn.javahome.frank.cdp.api.model;

public record InternalJobResponse(
        boolean accepted,
        String externalJobId,
        String message
) {
}
