package me.gaoliang.tinyrpc.consumer;

import io.netty.channel.DefaultEventLoop;
import io.netty.util.concurrent.DefaultPromise;
import me.gaoliang.tinyrpc.core.common.TinyRpcFuture;
import me.gaoliang.tinyrpc.core.common.TinyRpcRequest;
import me.gaoliang.tinyrpc.core.common.TinyRpcRequestHolder;
import me.gaoliang.tinyrpc.core.common.TinyRpcResponse;
import me.gaoliang.tinyrpc.protocol.protocol.MsgHeader;
import me.gaoliang.tinyrpc.protocol.protocol.MsgType;
import me.gaoliang.tinyrpc.protocol.protocol.ProtocolConstants;
import me.gaoliang.tinyrpc.protocol.protocol.TinyRpcProtocol;
import me.gaoliang.tinyrpc.protocol.serialization.SerializationTypeEnum;
import me.gaoliang.tinyrpc.registry.RegistryService;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

public class TinyRpcInvocationProxy implements InvocationHandler {
    private final String serviceVersion;
    private final long timeout;
    private final RegistryService registryService;

    public TinyRpcInvocationProxy(String serviceVersion, long timeout, RegistryService registryService) {
        this.serviceVersion = serviceVersion;
        this.timeout = timeout;
        this.registryService = registryService;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        TinyRpcProtocol<TinyRpcRequest> protocol = new TinyRpcProtocol<>();
        MsgHeader header = new MsgHeader();
        long requestId = TinyRpcRequestHolder.REQUEST_ID_GEN.incrementAndGet();
        header.setMagic(ProtocolConstants.MAGIC);
        header.setRequestId(requestId);
        header.setSerialization((byte) SerializationTypeEnum.JSON.getType());
        header.setMsgType((byte) MsgType.REQUEST.getType());
        header.setStatus((byte) 0x1);
        protocol.setHeader(header);

        TinyRpcRequest request = new TinyRpcRequest();
        request.setServiceVersion(this.serviceVersion);
        request.setClassName(method.getDeclaringClass().getName());
        request.setMethodName(method.getName());
        request.setParameterTypes(method.getParameterTypes());
        request.setParams(args);
        protocol.setBody(request);

        TinyRpcConsumer rpcConsumer = new TinyRpcConsumer();
        TinyRpcFuture<TinyRpcResponse> future = new TinyRpcFuture<>(new DefaultPromise<>(new DefaultEventLoop()), timeout);

        rpcConsumer.sendRequest(protocol, this.registryService);
        TinyRpcRequestHolder.REQUEST_MAP.put(requestId, future);

        return future.getPromise().get(future.getTimeout(), TimeUnit.MILLISECONDS).getData();
    }
}
