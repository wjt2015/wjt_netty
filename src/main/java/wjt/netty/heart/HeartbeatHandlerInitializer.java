package wjt.netty.heart;

import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.MyIdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Slf4j
@Component
public class HeartbeatHandlerInitializer extends ChannelInitializer<SocketChannel> {


    @Resource
    private MyIdleStateHandler myIdleStateHandler;

    @Resource
    private ChannelInboundHandler heartbeatServerHandler;

    @Resource
    private WjtIdleStateHandler wjtIdleStateHandler;


    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        //pipeline.addLast(myIdleStateHandler).addLast(heartbeatServerHandler);
        pipeline.addLast(wjtIdleStateHandler).addLast(heartbeatServerHandler);
        log.info("pipeline={};", pipeline);
    }
}
