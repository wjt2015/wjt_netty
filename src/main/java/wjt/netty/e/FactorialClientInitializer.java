package wjt.netty.e;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.compression.ZlibCodecFactory;
import io.netty.handler.codec.compression.ZlibWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Slf4j
@Service
public class FactorialClientInitializer extends ChannelInitializer<SocketChannel> {

    @Resource
    private FactorialClientHandler factorialClientHandler;

    @Resource
    private NumEncoder numEncoder;

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        //stream compression;
        pipeline.addLast(ZlibCodecFactory.newZlibEncoder(ZlibWrapper.GZIP));
        pipeline.addLast(ZlibCodecFactory.newZlibDecoder(ZlibWrapper.GZIP));

        //num codec;
        pipeline.addLast(new BigIntegerDecoder());
        pipeline.addLast(numEncoder);

        //business logic;
        pipeline.addLast(factorialClientHandler);

        log.info("pipeline={};",pipeline);
    }
}