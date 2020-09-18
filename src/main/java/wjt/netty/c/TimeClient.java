package wjt.netty.c;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
public class TimeClient {

    private String host="127.0.0.1";
    private int port=10003;

    @Resource
    private TimeClientHandler timeClientHandler;

    public void run(){
        EventLoopGroup group=new NioEventLoopGroup();
        try {
            Bootstrap b=new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE,true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast("timeClientHandler",timeClientHandler);
                        }
                    });
            ChannelFuture f = b.connect(host, port).sync();
            log.info("time client connect successfully!");
            f.channel().closeFuture().sync();
            log.info("time client close!");
        }catch (Exception e){
            log.error("time client error!",e);
        }finally {
            group.shutdownGracefully();
            log.info("time client  shutdown gracefully!");
        }

    }

}
