package me.gaoliang.tinyrpc.consumer;

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

    private Class<?> interfaceClass;
    private String serviceVersion;
    private String registryType;
    private String registryAddress;
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
        RegistryService registryService = RegistryFactory.getInstance(this.registryAddress, RegistryType.valueOf(this.registryType));
        this.object = Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class<?>[]{interfaceClass},
                new TinyRpcInvocationProxy(serviceVersion, registryService)
        );
    }
}
