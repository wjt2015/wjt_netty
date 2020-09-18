package wjt.netty.d;

import com.google.common.base.Strings;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.BufferedReader;
import java.io.InputStreamReader;

@Slf4j
@Service
public class TelnetClient {

    private final String host = "127.0.0.1";
    private final int port = 10004;

    @Resource
    private TelnetClientInitializer telnetClientInitializer;

    public void run() {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            Channel ch = b.group(group)
                    .channel(NioSocketChannel.class)
                    .handler(telnetClientInitializer).connect(host, port).channel();
            //read cmds from the stdin;
            ChannelFuture lastWriteFuture = null;
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
                while (true) {
                    String line = reader.readLine();
                    if (Strings.isNullOrEmpty(line)) {
                        break;
                    }

                    final ByteBuf byteBuf = Unpooled.buffer(256);
                    byteBuf.writeCharSequence(line + "\r\n", CharsetUtil.UTF_8);
                    lastWriteFuture = ch.writeAndFlush(byteBuf);
                    if (line.equalsIgnoreCase("bve")) {
                        //wait until the server closes the connection;
                        ch.closeFuture().sync();
                        break;
                    }
                }

                if (lastWriteFuture != null) {
                    //wait until all msgs are flushed before closing the channel;
                    lastWriteFuture.sync();
                }

            } catch (Exception e) {
                log.error("io error!", e);
            }

        } catch (Exception e) {
            log.error("client error!", e);
        } finally {
            group.shutdownGracefully();
            log.info("client shutdown gracefully!");
        }


    }

}
