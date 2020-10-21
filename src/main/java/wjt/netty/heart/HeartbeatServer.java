package wjt.netty.heart;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 心跳服务器;
 * 客户端:telnet 127.0.0.1 10009
 */
@Slf4j
@Service
public class HeartbeatServer {
    private final int port;

    @Resource
    private HeartbeatHandlerInitializer heartbeatHandlerInitializer;


    public HeartbeatServer(int port) {
        this.port = port;
    }

    public HeartbeatServer() {
        this(HeartCommon.SERVER_PORT);
    }

    public void run() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        ServerBootstrap b = new ServerBootstrap();
        try {
            b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(heartbeatHandlerInitializer);

            ChannelFuture f = b.bind(port).sync();
            log.info("server bind successfully!b={};port={};", b, port);

            f.channel().closeFuture().sync().addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    log.info("server close complete!future={};", future);
                }
            });

        } catch (Exception e) {
            log.error("server error!", e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            log.info("server eventLoopGroup shutdown!b={};", b);
        }
    }

}
