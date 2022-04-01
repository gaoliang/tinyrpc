package me.gaoliang.tinyrpc.provider;

import me.gaoliang.tinyrpc.core.RpcProperties;
import me.gaoliang.tinyrpc.registry.RegistryFactory;
import me.gaoliang.tinyrpc.registry.RegistryService;
import me.gaoliang.tinyrpc.registry.RegistryType;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @author gaoliang
 */
@Configuration
@EnableConfigurationProperties(RpcProperties.class)
public class RpcProviderAutoConfiguration {

    @Resource
    private RpcProperties rpcProperties;

    @Bean
    public RpcProvider init() throws Exception {
        RegistryType type = RegistryType.valueOf(rpcProperties.getRegistryType());
        RegistryService serviceRegistry = RegistryFactory.getInstance(rpcProperties.getRegistryAddress(), type);
        return new RpcProvider(rpcProperties.getServicePort(), serviceRegistry);
    }

}