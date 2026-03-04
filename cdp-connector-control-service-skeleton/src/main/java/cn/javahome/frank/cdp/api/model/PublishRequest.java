package cn.javahome.frank.cdp.api.model;

public record PublishRequest(
        TaskMode mode,
        Boolean force
) {
}
