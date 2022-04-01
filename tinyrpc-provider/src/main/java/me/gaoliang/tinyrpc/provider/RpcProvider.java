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
import me.gaoliang.tinyrpc.core.RpcServiceUtils;
import me.gaoliang.tinyrpc.core.ServiceMeta;
import me.gaoliang.tinyrpc.protocol.codec.TinyRpcDecoder;
import me.gaoliang.tinyrpc.protocol.codec.TinyRpcEncoder;
import me.gaoliang.tinyrpc.protocol.handler.RpcRequestHandler;
import me.gaoliang.tinyrpc.provider.annotion.TinyRpcService;
import me.gaoliang.tinyrpc.registry.RegistryService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

/**
 * RPC service，使用 Netty 实现 I/O 事件循环。
 * 使用 InitializingBean.afterPropertiesSet 来创建并启动 Netty
 *
 * @author gaoliang
 */
@Slf4j
public class RpcProvider implements InitializingBean, BeanPostProcessor {
    private final int serverPort;
    private final RegistryService registryService;
    private final String serverAddress;

    /**
     * 服务名到 bean
     * maybe use spring context to load?
     */
    private Map<String, Object> rpcServiceHolder = new HashMap<>();


    public RpcProvider(int serverPort, RegistryService registryService) throws UnknownHostException {
        this.serverPort = serverPort;
        this.registryService = registryService;
        this.serverAddress = InetAddress.getLocalHost().getHostAddress();
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
                    ch.pipeline()
                            .addLast(new TinyRpcEncoder())
                            .addLast(new TinyRpcDecoder())
                            .addLast(new RpcRequestHandler(rpcServiceHolder));
                }
            }).childOption(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture channelFuture = bootstrap.bind(this.serverPort).sync();
            log.info("tiny rpc server start success! port: {}", this.serverPort);
            channelFuture.channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully().sync();
            workerGroup.shutdownGracefully().sync();
        }
    }

    /**
     * 对所有初始化完成后的 Bean 进行扫描
     * 如果 Bean 包含 @RpcService 注解，那么通过注解读取服务的元数据信息并构造出 ServiceMeta 对象，并将服务的元数据信息发布至注册中心
     * 同时将服务信息存储到 map，用于快速查找对应的服务实现。
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        TinyRpcService rpcService = bean.getClass().getAnnotation(TinyRpcService.class);
        if (rpcService != null) {
            String serviceName = rpcService.serviceInterface().getName();
            String serviceVersion = rpcService.serviceVersion();
            ServiceMeta serviceMeta = new ServiceMeta();
            serviceMeta.setServiceAddress(serverAddress);
            serviceMeta.setServicePort(serverPort);
            serviceMeta.setServiceName(serviceName);
            serviceMeta.setServiceVersion(serviceVersion);
            String serviceKey = RpcServiceUtils.buildServiceKey(serviceMeta);
            try {
                registryService.register(serviceMeta);
                rpcServiceHolder.put(serviceKey, bean);
                log.info("successfully registered {}", serviceKey);
            } catch (Exception e) {
                log.error("failed to register service {}#{}", serviceKey, serviceVersion, e);
            }
        }
        return bean;
    }

}
