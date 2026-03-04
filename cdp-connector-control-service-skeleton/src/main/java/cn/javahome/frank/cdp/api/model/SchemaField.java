package cn.javahome.frank.cdp.api.model;

public record SchemaField(
        String name,
        String type,
        Boolean nullable,
        String comment
) {
}
