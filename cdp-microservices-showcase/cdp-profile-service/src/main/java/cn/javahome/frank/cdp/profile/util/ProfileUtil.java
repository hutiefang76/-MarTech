package cn.javahome.frank.cdp.profile.util;

import java.util.UUID;

/**
 * 简化工具类：封装统一ID生成等基础逻辑。
 */
public final class ProfileUtil {

    private ProfileUtil() {
    }

    public static String buildId(String prefix) {
        return prefix + "-" + UUID.randomUUID();
    }
}
