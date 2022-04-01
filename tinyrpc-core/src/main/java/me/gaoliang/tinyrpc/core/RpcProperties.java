package me.gaoliang.tinyrpc.core;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author gaoliang
 */
@Data
@ConfigurationProperties(prefix = "tinyrpc.provider")
public class RpcProperties {

    private int servicePort;

    private String registryAddress;

    private String registryType;

}
