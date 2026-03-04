package cn.javahome.frank.cdp.api.model;

public record BaseResponse(
        String code,
        String message
) {
    public static BaseResponse success() {
        return new BaseResponse("0", "success");
    }
}

