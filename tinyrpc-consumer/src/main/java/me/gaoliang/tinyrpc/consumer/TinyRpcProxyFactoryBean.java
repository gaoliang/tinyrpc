package me.gaoliang.tinyrpc.consumer;

import lombok.extern.slf4j.Slf4j;
import me.gaoliang.tinyrpc.registry.RegistryFactory;
import me.gaoliang.tinyrpc.registry.RegistryService;
import me.gaoliang.tinyrpc.registry.RegistryType;
import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.Proxy;

/**
 * TinyRpc 代理的 FactoryBean
 *
 * @author gaoliang
 */
@Slf4j
public class TinyRpcProxyFactoryBean implements FactoryBean<Object> {
    public Class<?> getInterfaceClass() {
        return interfaceClass;
    }

    public void setInterfaceClass(Class<?> interfaceClass) {
        this.interfaceClass = interfaceClass;
    }

    public String getServiceVersion() {
        return serviceVersion;
    }

    public void setServiceVersion(String serviceVersion) {
        this.serviceVersion = serviceVersion;
    }

    public String getRegistryType() {
        return registryType;
    }

    public void setRegistryType(String registryType) {
        this.registryType = registryType;
    }

    public String getRegistryAddress() {
        return registryAddress;
    }

    public void setRegistryAddress(String registryAddress) {
        this.registryAddress = registryAddress;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    private Class<?> interfaceClass;
    private String serviceVersion;
    private String registryType;
    private String registryAddress;
    private long timeout;
    private Object object;

    @Override
    public Object getObject() throws Exception {
        return object;

    }

    @Override
    public Class<?> getObjectType() {
        return interfaceClass;
    }

    /**
     * 初始化动态代理类
     *
     * @throws Exception
     */
    public void init() throws Exception {
        log.info("generate proxy bean for class {}", this.interfaceClass);
        RegistryService registryService = RegistryFactory.getInstance(this.registryAddress, RegistryType.valueOf(this.registryType));
        this.object = Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class<?>[]{interfaceClass},
                new TinyRpcInvocationProxy(serviceVersion, timeout, registryService)
        );
    }
}
