package me.gaoliang.tinyrpc.consumer;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author gaoliang
 */
@ConfigurationProperties(prefix = "tinyrpc.consumer")
public class TinyRpcConsumerProperties {
    String registryServer;
    String registryType;
}
