package me.gaoliang.tinyrpc.registry;

import me.gaoliang.tinyrpc.core.ServiceMeta;

public class NacosRegistryService implements RegistryService{
    @Override
    public void register(ServiceMeta serviceMeta) {

    }

    @Override
    public void unRegister(ServiceMeta serviceMeta) {

    }

    @Override
    public ServiceMeta discovery(String serviceName, int invokerHashcode) {
        return null;
    }

    @Override
    public void destroy() {

    }
}
