# Tiny RPC

## 协议
```
    +---------------------------------------------------------------+
    | 魔数 2byte | 协议版本号 1byte | 序列化算法 1byte | 报文类型 1byte  |
    +---------------------------------------------------------------+
    | 状态 1byte |        消息 ID 8byte     |      数据长度 4byte     |
    +---------------------------------------------------------------+
    |                   数据内容 （长度不定）                          |
    +---------------------------------------------------------------+
```

## Features
- [x] I/O 线程、业务线程分离（基于 Netty）
- [x] ZooKeeper 服务注册（基于 curator)
- [x] 一致性 hash （环形hash）
- [x] Spring Boot 集成
- [x] Json、Hessian 序列化

## TODO
- [ ] 服务 unregister 
- [ ] 序列化协议、业务线程池支持配置。
- [ ] 支持 nacos 注册中心
- [ ] 缓存和复用 TCP 连接
- [ ] retry 机制
- [ ] 探活 + 心跳机制，避免假死服务
- [ ] 支持 CGLib 动态代理
- [ ] 容错机制
- [ ] 便于引入的 Maven 依赖