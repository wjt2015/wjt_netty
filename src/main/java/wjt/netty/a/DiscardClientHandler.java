package wjt.netty.a;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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
        this.content = ctx.alloc().directBuffer(1024).writeZero(1024);
        log.info("ctx={};content={};", ctx, content);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        this.content.release();
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

    }


    private void generateTraffic() {
        ctx.writeAndFlush(content.duplicate().retain()).addListener(new ChannelFutureListener() {
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
        });
    }

}
