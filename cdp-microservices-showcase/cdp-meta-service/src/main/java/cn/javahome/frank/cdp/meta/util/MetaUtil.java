package cn.javahome.frank.cdp.meta.util;

import java.util.UUID;

/**
 * 简化工具类：封装统一ID生成等基础逻辑。
 */
public final class MetaUtil {

    private MetaUtil() {
    }

    public static String buildId(String prefix) {
        return prefix + "-" + UUID.randomUUID();
    }
}
