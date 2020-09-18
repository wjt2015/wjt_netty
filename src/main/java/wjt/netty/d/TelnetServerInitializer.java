package wjt.netty.d;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
public class TelnetServerInitializer extends ChannelInitializer<SocketChannel> {

    private static final StringDecoder STRING_DECODER=new StringDecoder();
    private static final StringEncoder STRING_ENCODER=new StringEncoder();

    @Resource
    private TelnetServerHandler telnetServerHandler;


    @Override
    protected void initChannel(SocketChannel ch) throws Exception {

        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter()))
        .addLast(STRING_DECODER).addLast(STRING_ENCODER).addLast(telnetServerHandler);

        log.info("pipeline={};",pipeline);

    }
}
