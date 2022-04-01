package me.gaoliang.tinyrpc.protocol.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import me.gaoliang.tinyrpc.core.common.TinyRpcRequestHolder;
import me.gaoliang.tinyrpc.core.common.TinyRpcFuture;
import me.gaoliang.tinyrpc.core.common.TinyRpcResponse;
import me.gaoliang.tinyrpc.protocol.protocol.TinyRpcProtocol;

/**
 * 响应入站处理器，把对应的 promise 设置为 success 状态。
 */
@Slf4j
public class RpcResponseHandler extends SimpleChannelInboundHandler<TinyRpcProtocol<TinyRpcResponse>> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TinyRpcProtocol<TinyRpcResponse> msg) {
        long requestId = msg.getHeader().getRequestId();
        log.info("received response for request: {}, response is : {}", requestId, msg.getBody());
        TinyRpcFuture<TinyRpcResponse> future = TinyRpcRequestHolder.REQUEST_MAP.remove(requestId);
        future.getPromise().setSuccess(msg.getBody());
        log.info("close connection...");
        ctx.channel().close();
    }
}