package wjt.netty.d;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@ChannelHandler.Sharable
public class TelnetServerHandler implements ChannelInboundHandler {


    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        log.info("连接建立!ctx={};", ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        ctx.close();
        log.info("断开连接!ctx={};", ctx);
    }

    private void sendMsg(ChannelHandlerContext ctx){
        final ByteBuf byteBuf = ctx.alloc().directBuffer(1024);
        byteBuf.writeCharSequence("欢迎你,netty!", CharsetUtil.UTF_8);
        log.info("before_writeAndFlush!ctx={};byteBuf={};", ctx, byteBuf);
        ctx.writeAndFlush(byteBuf);
        log.info("after_writeAndFlush!ctx={};byteBuf={};", ctx, byteBuf);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (msg instanceof ByteBuf) {
            final ByteBuf byteBuf = (ByteBuf) msg;
            log.info("ctx={};byteBuf={};", ctx, byteBuf.toString(CharsetUtil.UTF_8));
            byteBuf.release();
        } else {
            log.info("ctx={};msg={};", ctx, msg);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        log.info("read_complete;ctx={};",ctx);

        sendMsg(ctx);
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
