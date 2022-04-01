package me.gaoliang.tinyrpc.registry;

import me.gaoliang.tinyrpc.core.ServiceMeta;

public interface RegistryService {
    void register(ServiceMeta serviceMeta) throws Exception;

    void unRegister(ServiceMeta serviceMeta) throws Exception;

    ServiceMeta discovery(String serviceName) throws Exception;

    void destroy();

}
