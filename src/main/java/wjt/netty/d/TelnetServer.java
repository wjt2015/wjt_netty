package wjt.netty.d;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
public class TelnetServer {
    @Value("10004")
    private int port;

    @Resource
    private TelnetServerInitializer telnetServerInitializer;

    public void run(){
        EventLoopGroup bossGroup=new NioEventLoopGroup();
        EventLoopGroup workerGroup=new NioEventLoopGroup();

        try {
            ServerBootstrap b=new ServerBootstrap();
            b.group(bossGroup,workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(telnetServerInitializer);

            b.bind(port).sync().channel().closeFuture().sync();
            log.info("server close!");
        }catch (Exception e){
            log.error("server error!",e);
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            log.info("server shutdown gracefully!");
        }
    }

}
