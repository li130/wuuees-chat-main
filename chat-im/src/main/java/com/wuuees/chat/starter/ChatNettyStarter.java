package com.wuuees.chat.starter;

import com.wuuees.chat.codec.MessageDecoder;
import com.wuuees.chat.codec.MessageEncoder;
import com.wuuees.chat.common.utils.ThreadPoolUtil;
import com.wuuees.chat.config.ChatConfig;
import com.wuuees.chat.handler.HearBeatHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;

@SpringBootConfiguration
@Slf4j
public class ChatNettyStarter implements InitializingBean {

    @Autowired
    private ChatConfig chatConfig;

    @Autowired
    private MessageDecoder messageDecoder;

    @Autowired
    private MessageEncoder messageEncoder;

    @Autowired
    private HearBeatHandler hearBeatHandler;

    private void startImApplication() throws InterruptedException {
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(chatConfig.getBossThreadSize());
        NioEventLoopGroup workerGroup = new NioEventLoopGroup(chatConfig.getWorkThreadSize());
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new HttpServerCodec());
                        ch.pipeline().addLast(new ChunkedWriteHandler());
                        ch.pipeline().addLast(new HttpObjectAggregator(65535));
                        ch.pipeline().addLast(new WebSocketServerProtocolHandler("/ws"));
                        ch.pipeline().addLast(new IdleStateHandler(0, 0, 10));
                        ch.pipeline().addLast(messageDecoder);
                        ch.pipeline().addLast(messageEncoder);
                        ch.pipeline().addLast(hearBeatHandler);
                    }
                });

        // 注册一个虚拟机关闭的钩子
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("JVM关闭钩子触发,开始关闭netty服务以及线程池...");
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            log.info("netty服务关闭完成");
            // 线程池关闭
            ThreadPoolUtil.me().shutdown();
            log.info("线程池关闭完成");
        }));

        ChannelFuture channelFuture = bootstrap.bind(chatConfig.getWebsocketPort()).sync();
        log.info("netty服务启动成功,监听端口：{}", chatConfig.getWebsocketPort());
        channelFuture.channel().closeFuture();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Thread thread = new Thread(() -> {
            try {
                startImApplication();
            } catch (InterruptedException e) {
                log.error("netty服务启动失败:{}", e.getMessage());
            }
        });
        thread.setName("Netty-Server-Thread");
        thread.start();
    }
}
