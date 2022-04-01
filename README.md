# Tiny RPC use netty

## consumer

### 如何实现 RPC 代理

启动时，扫描所有 bean 中注解了 @TinyRpcReference 的属性，拿到注解的参数后，构造一个 BeanDefinition 放入 BeanFactory 中生成一个对应接口的 JDK 动态代理，实现代理类进入 Spring
IoC。

### 如何实现对远程服务的调用和超时控制？

动态代理类拦截所有对 bean 的方法调用，封装为 RpcProtocol 对象，RpcProtocol 中包含请求的自增 RequestId， 构造 RpcConsumer , RpcConsumer 使用 Netty
编码后异步发送到服务提供方，返回 Future 对象，并把这个 Future 对象和 RequestId 和 Future 对象关联。 方法阻塞在 future.get(timeout) 中，等待服务提供方发送回响应数据。

在 RpcConsumer 连接的 Netty Channel Pipeline 中，还注册了 RpcProtocolDecoder 和 RpcResponseHandler，在收到服务端响应后，解析出 Request Id，
resolve 对应的 Future，方法调用返回。

如果方法超时，则 future.get(timeout) 会抛出异常，提示调用方调用超时。