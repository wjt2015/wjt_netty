package wjt.netty.a;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
public class DiscardClient {

    public static final boolean SSL = System.getProperty("ssl") != null;
    public static final String HOST = System.getProperty("host", "127.0.0.1");
    public static final int PORT = Integer.parseInt(System.getProperty("port", "10001"));
    public static final int SIZE = Integer.parseInt(System.getProperty("size", "256"));

    @Resource
    private DiscardClientHandler discardClientHandler;


    public void run() {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast("discardClientHandler", discardClientHandler);
                        }
                    });

            ChannelFuture f = b.connect(HOST, PORT).sync();

            log.info("client connected!HOST={};PORT={};", HOST, PORT);
            f.channel().closeFuture().sync();
            log.info("client closeFuture!");

        } catch (Exception e) {
            log.error("client error!", e);
        } finally {
            group.shutdownGracefully();
            log.info("client shutdown gracefully!");
        }
    }

}
