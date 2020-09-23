package wjt.netty.f;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.InetSocketAddress;

@Slf4j
@Service
public class UDPReceiver {

    private final int port;
    private final InetSocketAddress destAddr;

    public UDPReceiver(int port, InetSocketAddress destAddr) {
        this.port = port;
        this.destAddr = destAddr;
    }

    public void run() {
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();
        try {
            b.group(group)
                    .channel(NioDatagramChannel.class)
                    .option(ChannelOption.SO_BROADCAST, true)
                    .handler(new ChannelInitializer<NioDatagramChannel>() {
                        @Override
                        protected void initChannel(NioDatagramChannel ch) throws Exception {
                            ch.pipeline().addLast(new UDPDecoder()).addLast(new UDPEncoder());
                        }
                    });
            ChannelFuture f = b.bind(port).sync();
            log.info("udp receiver bind successfully!port={};", port);
            f.channel().closeFuture().sync();
            log.info("udp receiver close!");
        } catch (Exception e) {
            log.error("udp receiver error!", e);
        } finally {
            group.shutdownGracefully();
        }
    }

}
