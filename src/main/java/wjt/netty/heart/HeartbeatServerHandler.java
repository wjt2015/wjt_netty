package wjt.netty.heart;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HeartbeatServerHandler implements ChannelInboundHandler {


    private static final ByteBuf HEARTBEAT_SEQUENCE = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer("Heart_Beat", CharsetUtil.UTF_8));

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("ctx={};", ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("ctx={};", ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        log.info("ctx={};msg={};", ctx, msg);
        ctx.fireChannelRead(msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        log.info("ctx={};", ctx);
        ctx.fireChannelReadComplete();
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        String type = "";
        log.info("ctx={};evt={};", ctx, evt);
        if (!(evt instanceof IdleStateEvent)) {
            ctx.fireUserEventTriggered(evt);
        } else {
            IdleStateEvent event = (IdleStateEvent) evt;
            switch (event.state()) {
                case ALL_IDLE:
                    type = "all idle";
                    break;
                case READER_IDLE:
                    type = "read_idle";
                    break;
                case WRITER_IDLE:
                    type = "write_idle";
                    break;
            }
            log.info("ctx={};evt={};type={};", ctx, evt, type);
            //发送心跳;
            ctx.writeAndFlush(HEARTBEAT_SEQUENCE.duplicate()).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
        }


    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        log.info("ctx={};", ctx);
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
}
