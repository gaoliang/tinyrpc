package me.gaoliang.tinyrpc.consumer;

import me.gaoliang.tinyrpc.registry.RegistryService;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class TinyRpcInvocationProxy implements InvocationHandler {
    private final String serviceVersion;
    private final RegistryService registryService;

    public TinyRpcInvocationProxy(String serviceVersion, RegistryService registryService) {
        this.serviceVersion = serviceVersion;
        this.registryService = registryService;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // TODO call remote service.
        return "hello world 123";
    }
}
