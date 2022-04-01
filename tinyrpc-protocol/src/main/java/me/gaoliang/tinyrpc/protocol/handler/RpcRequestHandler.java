package me.gaoliang.tinyrpc.protocol.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import me.gaoliang.tinyrpc.core.RpcServiceUtils;
import me.gaoliang.tinyrpc.core.common.TinyRpcRequest;
import me.gaoliang.tinyrpc.core.common.TinyRpcResponse;
import me.gaoliang.tinyrpc.protocol.protocol.MsgHeader;
import me.gaoliang.tinyrpc.protocol.protocol.MsgType;
import me.gaoliang.tinyrpc.protocol.protocol.TinyRpcProtocol;
import org.springframework.cglib.reflect.FastClass;

import java.util.Map;

/**
 * Netty 请求入站处理器
 */
@Slf4j
public class RpcRequestHandler extends SimpleChannelInboundHandler<TinyRpcProtocol<TinyRpcRequest>> {

    private final Map<String, Object> rpcServiceMap;

    public RpcRequestHandler(Map<String, Object> rpcServiceMap) {
        this.rpcServiceMap = rpcServiceMap;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TinyRpcProtocol<TinyRpcRequest> reqProtocol) {
        // 这里只是提交了任务，不会阻塞 I/O 线程，实际的业务处理是在业务线程池中执行的
        RpcRequestProcessor.submitRequest(() -> {
            log.info("received request: {}", reqProtocol);
            TinyRpcResponse response = new TinyRpcResponse();
            // 取出请求 header
            MsgHeader header = reqProtocol.getHeader();
            long requestId = header.getRequestId();
            try {
                Object result = handle(reqProtocol.getBody());
                response.setData(result);
                TinyRpcProtocol<TinyRpcResponse> resProtocol = new TinyRpcProtocol<>();
                // 修改 header 类型为 RESPONSE
                header.setMsgType((byte) MsgType.RESPONSE.getType());
                resProtocol.setHeader(header);
                resProtocol.setBody(response);
                ctx.writeAndFlush(resProtocol);
                log.info("process request: {} success, response: {}", requestId, result);
            } catch (Throwable throwable) {
                response.setMessage(throwable.toString());
                log.error("process request: {} error", requestId, throwable);
            }
        });
    }

    /**
     * 代理 Rpc request 到真实调用
     */
    private Object handle(TinyRpcRequest request) throws Throwable {
        String serviceKey = RpcServiceUtils.buildServiceKey(request.getClassName(), request.getServiceVersion());
        Object serviceBean = rpcServiceMap.get(serviceKey);

        log.info("invoke  {}", serviceKey);
        if (serviceBean == null) {
            throw new RuntimeException(String.format("service not exist: %s:%s", request.getClassName(), request.getMethodName()));
        }

        Class<?> serviceClass = serviceBean.getClass();
        String methodName = request.getMethodName();
        Class<?>[] parameterTypes = request.getParameterTypes();
        Object[] parameters = request.getParams();

        FastClass fastClass = FastClass.create(serviceClass);
        int methodIndex = fastClass.getIndex(methodName, parameterTypes);
        return fastClass.invoke(methodIndex, serviceBean, parameters);
    }
}
