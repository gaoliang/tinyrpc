package me.gaoliang.tinyrpc.consumer;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import me.gaoliang.tinyrpc.core.RpcServiceUtils;
import me.gaoliang.tinyrpc.core.ServiceMeta;
import me.gaoliang.tinyrpc.core.common.TinyRpcRequest;
import me.gaoliang.tinyrpc.protocol.codec.TinyRpcDecoder;
import me.gaoliang.tinyrpc.protocol.codec.TinyRpcEncoder;
import me.gaoliang.tinyrpc.protocol.handler.RpcResponseHandler;
import me.gaoliang.tinyrpc.protocol.protocol.TinyRpcProtocol;
import me.gaoliang.tinyrpc.registry.RegistryService;

/**
 * 每个 server reference 对应一个 TinyRpc Consumer，使用 Netty 实现。
 * @author gaoliang
 */
@Slf4j
public class TinyRpcConsumer {

    private Bootstrap bootstrap;

    private EventLoopGroup eventLoopGroup;

    public TinyRpcConsumer() {
        bootstrap = new Bootstrap();
        eventLoopGroup = new NioEventLoopGroup(4);
        bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline()
                                .addLast(new TinyRpcEncoder())
                                .addLast(new TinyRpcDecoder())
                                .addLast(new RpcResponseHandler());
                    }
                });
    }

    /**
     * 发送请求，发送过程是异步的
     */
    public void sendRequest(TinyRpcProtocol<TinyRpcRequest> protocol, RegistryService registryService) throws Exception {
        TinyRpcRequest request = protocol.getBody();

        String serviceKey = RpcServiceUtils.buildServiceKey(request.getClassName(), request.getServiceVersion());
        Object[] params = request.getParams();
        int invokerHashCode = params.length > 0 ? params[0].hashCode() : serviceKey.hashCode();
        ServiceMeta serviceMetadata = registryService.discovery(serviceKey, invokerHashCode);

        if (serviceMetadata == null) {
            return;
        }

        ChannelFuture future = bootstrap.connect(serviceMetadata.getServiceAddress(), serviceMetadata.getServicePort()).sync();
        future.addListener((ChannelFutureListener) arg0 -> {
            if (future.isSuccess()) {
                log.info("connect rpc server {} on port {} success.", serviceMetadata.getServiceAddress(), serviceMetadata.getServicePort());
            } else {
                log.error("connect rpc server {} on port {} failed.", serviceMetadata.getServiceAddress(), serviceMetadata.getServicePort());
                future.cause().printStackTrace();
                eventLoopGroup.shutdownGracefully();
            }
        });
        future.channel().writeAndFlush(protocol);
    }
}
