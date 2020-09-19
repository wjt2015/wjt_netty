package wjt.netty.d;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
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
import java.util.List;

@Slf4j
@Service
public class TelnetClient {

    private final String host = "127.0.0.1";
    private final int port = 10004;

    @Resource
    private TelnetClientInitializer telnetClientInitializer;

    private List<String> lines = Lists.newArrayList("中标供应商对本次维修维保项目所更换的零件提供2年的保修服务，保修期自验收合格之日起计算",
            "现非故意破坏性故障，由成交供应商负责免费维修确保设备正常运转，若设备损坏经双方确认后需要更换的",
            "本项目维护保养的内容及基本要求均应遵守有关法规及标准，以及我校的各项管理制度。参与项目的所有人员需严格遵守国家及学校防疫工作要求，在校期间服从学校管理。");

    public void run() {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group)
                    .channel(NioSocketChannel.class).handler(telnetClientInitializer);
            //start the connection attempt;
            Channel ch = b.connect(host, port).sync().channel();
            ChannelFuture lastWriteFuture = null;

            for (String line : lines) {
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
            log.info("write and flush complete!");

        } catch (Exception e) {
            log.error("client error!", e);
        } finally {
            group.shutdownGracefully();
            log.info("client shutdown gracefully!");
        }


    }

}
