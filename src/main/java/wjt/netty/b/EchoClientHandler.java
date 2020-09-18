package wjt.netty.b;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@ChannelHandler.Sharable
public class EchoClientHandler implements ChannelInboundHandler {

    private final ByteBuf firstMsg;

    /**
     * 限制交互次数;
     */
    private final AtomicInteger count = new AtomicInteger(3);

    public EchoClientHandler() {
        firstMsg = Unpooled.buffer(1024);
        firstMsg.writeCharSequence("计算机软件技术", CharsetUtil.UTF_8);
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("writeAndFlush;ctx={};firstMsg={};", ctx, firstMsg);
        ctx.writeAndFlush(firstMsg);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {

    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        final ByteBuf byteBuf = (ByteBuf) msg;
        log.info("ctx={};byteBuf={};", ctx, byteBuf.toString(CharsetUtil.UTF_8));
        ctx.write(msg);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        log.info("ctx={};read complete!", ctx);
        ctx.flush();
        if (count.decrementAndGet() <= 0) {
            ctx.close();
        }
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
}
