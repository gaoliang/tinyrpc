package me.gaoliang.tinyrpc.core;

import lombok.Data;

/**
 * 服务元信息
 */
@Data
public class ServiceMeta {
    private String serviceName;
    private String serviceVersion;
    private String serviceAddress;
    private int servicePort;
}
