package wjt.netty.d;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
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


        }catch (Exception e){
            log.error("server error!",e);
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            log.info("server shutdown gracefully!");
        }


    }


}
