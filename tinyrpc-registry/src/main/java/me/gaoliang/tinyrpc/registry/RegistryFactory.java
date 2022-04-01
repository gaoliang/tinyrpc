package me.gaoliang.tinyrpc.registry;

/**
 * 注册中心工场
 * 双重检查加锁保证创建单例的注册中心
 */
public class RegistryFactory {
    private static volatile RegistryService registryService;

    public static RegistryService getInstance(String registryAddress, RegistryType registryType) throws Exception {
        if (null == registryService) {
            synchronized (RegistryFactory.class) {
                if (null == registryService) {
                    switch (registryType) {
                        case NACOS:
                            registryService = new NacosRegistryService();
                            break;
                        case ZOOKEEPER:
                            registryService = new ZookeeperRegistry(registryAddress);
                            break;
                    }
                }
            }
        }
        return registryService;
    }
}
