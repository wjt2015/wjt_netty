package wjt.netty.e;

import com.sun.tools.corba.se.idl.Factories;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
public class FactorialClient {



    @Resource
    private FactorialClientInitializer factorialClientInitializer;

    public void run(){
        EventLoopGroup group=new NioEventLoopGroup();

        try {

            Bootstrap b=new Bootstrap();
            ChannelFuture f = b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(factorialClientInitializer).connect(Common.HOST, Common.port).sync();
            log.info("connect successfully!b={};",b);
            f.channel().closeFuture().sync();
        }catch (Exception e){
            log.error("client error!",e);
        }finally {
            group.shutdownGracefully();
            log.info("group shutdown gracefully!");
        }


    }

}
