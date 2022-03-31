package me.gaoliang.tinyrpc.provider;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import me.gaoliang.tinyrpc.registry.RegistryService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * RPC service，使用 Netty 实现 I/O 事件循环。
 * 使用 InitializingBean.afterPropertiesSet 来创建并启动 Netty
 *
 * @author gaoliang
 */
@Slf4j
public class RpcProvider implements InitializingBean {
    private final String serverAddress;
    private final int serverPort;
    private final RegistryService registryService;

    public RpcProvider(String serverAddress, int serverPort, RegistryService registryService) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.registryService = registryService;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        new Thread(() -> {
            try {
                startRpcServer();
            } catch (Exception e) {
                log.error("failed to start tinyrpc service.", e);
            }
        }).start();
    }

    private void startRpcServer() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class).childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {

                }
            }).childOption(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture channelFuture = bootstrap.bind(this.serverAddress, this.serverPort).sync();
            log.info("tiny rpc server start success! server address: {}, port: {}", this.serverAddress, this.serverPort);
            channelFuture.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully().sync();
            workerGroup.shutdownGracefully().sync();
        }
    }
}
