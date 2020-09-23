package wjt.netty.f;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandler;
import io.netty.channel.ChannelPromise;
import lombok.extern.slf4j.Slf4j;

import java.net.SocketAddress;

@Slf4j
public class UDPOutHandler implements ChannelOutboundHandler {
    @Override
    public void bind(ChannelHandlerContext ctx, SocketAddress localAddress, ChannelPromise promise) throws Exception {
        log.info("before_bind!ctx={};localAddress={};", ctx, localAddress);
        ctx.bind(localAddress,promise);
        log.info("after_bind!ctx={};localAddress={};", ctx, localAddress);

    }

    @Override
    public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) throws Exception {
        log.info("ctx={};remoteAddress={};localAddress={};", ctx, remoteAddress, localAddress);
    }

    @Override
    public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        log.info("ctx={};", ctx);
    }

    @Override
    public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        log.info("ctx={};", ctx);
    }

    @Override
    public void deregister(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        log.info("ctx={};", ctx);
    }

    @Override
    public void read(ChannelHandlerContext ctx) throws Exception {
        log.info("ctx={};", ctx);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {

        log.info("ctx={};msg={};", ctx, msg);
        //传给下一个handler;
        ctx.write(msg);
    }

    @Override
    public void flush(ChannelHandlerContext ctx) throws Exception {
        log.info("ctx={};", ctx);
        ctx.flush();
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
