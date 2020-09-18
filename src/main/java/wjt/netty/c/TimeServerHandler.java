package wjt.netty.c;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@ChannelHandler.Sharable
public class TimeServerHandler implements ChannelInboundHandler {
    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        final DateTime now = new DateTime();
        final String data = now.toString("yyyy/MM/dd HH:mm:ss");
        sendMsg(ctx, data, new RedoListener(ctx, data, 3));
    }

    private void sendMsg(final ChannelHandlerContext ctx, final String data, final ChannelFutureListener channelFutureListener) {
        ctx.writeAndFlush(ctx.alloc().directBuffer(256)
                .writeCharSequence(data, CharsetUtil.UTF_8))
                .addListener(channelFutureListener);
    }

    static class RedoListener implements ChannelFutureListener {
        private final ChannelHandlerContext ctx;
        private final String data;
        private int redoCount;

        public RedoListener(ChannelHandlerContext ctx, String data, int redoCount) {
            this.ctx = ctx;
            this.data = data;
            this.redoCount = redoCount;
        }

        @Override
        public void operationComplete(ChannelFuture future) throws Exception {
            final boolean success = future.isSuccess();
            log.info("future={};success={};", future, success);
            if (success) {
                future.channel().close();
            } else if (--redoCount >= 0) {
                ctx.writeAndFlush(ctx.alloc().directBuffer(256).writeCharSequence(data, CharsetUtil.UTF_8))
                        .addListener(this);
            }
        }
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {

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
        log.error("ctx={};cause={};", ctx, cause);
    }
}
