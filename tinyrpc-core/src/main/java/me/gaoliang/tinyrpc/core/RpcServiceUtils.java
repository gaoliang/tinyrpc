package me.gaoliang.tinyrpc.core;

public class RpcServiceUtils {
    public static String buildServiceKey(ServiceMeta serviceMeta) {
        return String.join("#", serviceMeta.getServiceName(), serviceMeta.getServiceVersion());
    }

    public static String buildServiceKey(String serviceName, String serviceVersion) {
        return String.join("#",  serviceName, serviceVersion);
    }
}
