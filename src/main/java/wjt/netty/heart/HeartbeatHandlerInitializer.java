package wjt.netty.heart;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
public class HeartbeatHandlerInitializer extends ChannelInitializer<SocketChannel> {

    private static final int READ_IDLE_TIME_OUT=4;
    private static final int WRITE_IDLE_TIME_OUT=5;
    private static final int ALL_IDLE_TIME_OUT=7;



    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new IdleStateHandler(READ_IDLE_TIME_OUT,WRITE_IDLE_TIME_OUT,ALL_IDLE_TIME_OUT, TimeUnit.SECONDS))
        .addLast(new HeartbeatServerHandler());
    }
}
