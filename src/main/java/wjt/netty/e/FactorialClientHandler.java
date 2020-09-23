package wjt.netty.e;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.math.BigInteger;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
public class FactorialClientHandler extends SimpleChannelInboundHandler<BigInteger> {

    private ChannelHandlerContext ctx;
    private int recvMsgCount;
    private int next = 1;
    private final BlockingQueue<BigInteger> answers = new LinkedBlockingQueue<>();
    /**
     * 总的发送数字的个数;
     */
    private static final int SUM_COUNT = 5;

    /**
     * 每轮发送数字的个数;
     */
    private static final int COUNT_PER_LOOP = 3;


    public BigInteger getFactorial() {
        boolean interrupted = false;
        try {
            while (true) {
                try {
                    return answers.take();
                } catch (InterruptedException e) {
                    log.error("answers interrupted!", e);
                    interrupted = true;
                }
            }
        } finally {
            if (interrupted) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        this.ctx = ctx;
        log.info("ctx={};", ctx);
        sendNums();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("ctx error!ctx={};cause={};", ctx, cause);
        ctx.close();
    }

    private void sendNums() {
        ChannelFuture future = null;
        for (int i = 0; i < COUNT_PER_LOOP && next <= SUM_COUNT; i++) {
            //逐个发送;
            future = ctx.write(Integer.valueOf(next));
            next++;
        }
        if (next <= SUM_COUNT) {
            //全部发出完毕时添加listener;
            future.addListener(numSendListener);
            //ctx.close();
        }
        ctx.flush();
        log.info("sendNums finish!next={};ctx={};", next, ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, BigInteger msg) throws Exception {
        recvMsgCount++;
        log.info("ctx={};msg={};recvMsgCount={};", ctx, msg, recvMsgCount);
        if (recvMsgCount == SUM_COUNT) {
            ctx.close().addListener(f -> {
                boolean offer = answers.offer(msg);
                log.info("ctx={};msg={};offer={};", ctx, msg, offer);
            });

        }
    }

    static class MyChannelFutureListener implements ChannelFutureListener {
        private FactorialClientHandler factorialClientHandler;

        public MyChannelFutureListener(FactorialClientHandler factorialClientHandler) {
            this.factorialClientHandler = factorialClientHandler;
        }

        @Override
        public void operationComplete(ChannelFuture future) throws Exception {
            boolean success = future.isSuccess();
            log.info("sendNums finish!;success={};", success);
            if (success) {
                factorialClientHandler.sendNums();
            } else {
                log.error("sendNums error!cause={};", future.cause());
                future.channel().close();
            }
            factorialClientHandler.next = 1;
        }
    }

    private final ChannelFutureListener numSendListener = new MyChannelFutureListener(this);

}
