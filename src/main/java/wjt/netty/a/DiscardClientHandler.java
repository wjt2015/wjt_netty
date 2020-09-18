package wjt.netty.a;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@ChannelHandler.Sharable
public class DiscardClientHandler implements ChannelInboundHandler {

    private ByteBuf content;
    private ChannelHandlerContext ctx;


    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
        //init;
        this.content = ctx.alloc().directBuffer(DiscardClient.SIZE).writeZero(DiscardClient.SIZE);
        this.content.writeCharSequence("netty_best_network_framework!", CharsetUtil.UTF_8);
        log.info("ctx={};content={};", ctx, content);
        //send init msg;
        generateTraffic();
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        this.content.release();
        log.info("ctx={};", ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.info("ctx={};cause={};", ctx, cause);
        ctx.close();
    }


    private void generateTraffic() {
        final ByteBuf byteBuf = content.duplicate().retain();
        final String s = byteBuf.toString(CharsetUtil.UTF_8);
        log.info("s={};", s);
        //flush the outbound buffer to the socket;once flushed,generate the same amount of traffic again;
        ctx.writeAndFlush(byteBuf).addListener(trafficGenerator);
        log.info("write and flush!s={};", s);
    }


    private final ChannelFutureListener trafficGenerator = new ChannelFutureListener() {
        @Override
        public void operationComplete(ChannelFuture future) throws Exception {
            log.info("channel future complete!future={};success={};", future, future.isSuccess());
            if (future.isSuccess()) {
                generateTraffic();
            } else {
                log.info("future_cause={};", future.cause());
                future.channel().close();
            }
        }
    };

}
