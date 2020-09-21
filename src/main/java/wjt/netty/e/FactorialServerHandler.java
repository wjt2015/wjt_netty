package wjt.netty.e;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.math.BigInteger;

@Slf4j
public class FactorialServerHandler extends SimpleChannelInboundHandler<BigInteger> {

    private BigInteger lastMultiplier = new BigInteger("1");
    private BigInteger factorial = new BigInteger("1");

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, BigInteger msg) throws Exception {
        lastMultiplier = msg;
        factorial = factorial.multiply(lastMultiplier);
        ctx.writeAndFlush(factorial);
        log.info("read and write!ctx={};lastMultiplier={};factorial={};", ctx, lastMultiplier, factorial);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        log.info("conn active;ctx={};", ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        log.info("conn inactive;ctx={};lastMultiplier={};factorial={};", ctx, lastMultiplier, factorial);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("ctx={};cause={};", ctx, cause);
        ctx.close();
    }


}
