package me.gaoliang.tinyrpc.consumer;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author gaoliang
 */
@Configuration
@EnableConfigurationProperties({TinyRpcConsumerProperties.class})
public class TinyRpcConsumerAutoConfiguration {
}
