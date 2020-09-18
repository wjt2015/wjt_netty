package wjt.netty.b;

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
public class EchoClient {

    public static final boolean SSL = System.getProperty("ssl") != null;
    public static final String HOST = System.getProperty("host", "127.0.0.1");
    public static final int PORT = Integer.parseInt(System.getProperty("port", "10002"));
    public static final int SIZE = Integer.parseInt(System.getProperty("size", "256"));

    @Resource
    private EchoClientHandler echoClientHandler;

    public void run() {
        Bootstrap b = new Bootstrap();
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            b.group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ch.pipeline().addLast("echoClientHandler", echoClientHandler);
                        }
                    });

            ChannelFuture f = b.connect(HOST, PORT).sync();
            log.info("echo client connect successfully!f={};", f);
            f.channel().closeFuture().sync();
            log.info("echo client close!f={};", f);
        } catch (Exception e) {
            log.error("echo client error!", e);
        } finally {
            group.shutdownGracefully();
            log.info("echo client shutdown gracefully!");
        }


    }


}
