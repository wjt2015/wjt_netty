package wjt.netty.heart;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelOutboundHandler;
import io.netty.channel.ChannelPromise;
import io.netty.handler.timeout.MyIdleStateEvent;
import io.netty.util.concurrent.ScheduledFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.SocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * 仿写版的IdleStateHandler;
 */
@Slf4j
@Service
public class WjtIdleStateHandler implements ChannelInboundHandler, ChannelOutboundHandler {

    private long lastReadTime;
    private long lastWriteTime;

    private long readIdleDuration;
    private long writeIdleDuration;
    private long allIdleDuration;

    private boolean reading;

    private ScheduledFuture<?> readIdleFuture;
    private ScheduledFuture<?> writeIdleFuture;
    private ScheduledFuture<?> allIdleFuture;

    private boolean firstReadIdle = true;
    private boolean firstWriteIdle = true;
    private boolean firstAllIdle = true;
    /**
     * 防止多次初始化或撤销;
     * 0--none;1--initialized;2--destroyed;
     */
    private volatile int state;

    public WjtIdleStateHandler(long readIdleDuration, long writeIdleDuration, long allIdleDuration) {
        this.readIdleDuration = readIdleDuration;
        this.writeIdleDuration = writeIdleDuration;
        this.allIdleDuration = allIdleDuration;
    }


    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        if (ctx.channel().isActive()) {
            initialize(ctx);
        }
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        destroy();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //添加定时器;
        initialize(ctx);
    }

    private void initialize(final ChannelHandlerContext ctx) {
        /**
         * 判断状态,避免初始化/撤销互相冲突;
         */
        switch (state) {
            case 1:
            case 2:
                return;
        }
        state = 1;

        /**
         * 初始化定时器;
         */
        lastReadTime = lastWriteTime = System.currentTimeMillis();
        if (readIdleDuration > 0L) {
            readIdleFuture = ctx.executor().schedule(new ReadIdleTask(ctx), readIdleDuration, TimeUnit.MILLISECONDS);
        }
        if (writeIdleDuration > 0L) {
            writeIdleFuture = ctx.executor().schedule(new WriteIdleTask(ctx), writeIdleDuration, TimeUnit.MILLISECONDS);
        }
        if (allIdleDuration > 0L) {
            allIdleFuture = ctx.executor().schedule(new AllIdleTask(ctx), allIdleDuration, TimeUnit.MILLISECONDS);
        }
        log.info("init finish!readIdleFuture={};writeIdleDuration={};allIdleDuration={};", readIdleFuture, writeIdleFuture, allIdleFuture);
    }

    private void destroy() {
        switch (state) {
            case 2:
                return;
        }
        state = 2;
        /**
         * 撤销定时器任务;
         */
        if (readIdleFuture != null) {
            readIdleFuture.cancel(false);
            readIdleFuture = null;
        }
        if (writeIdleFuture != null) {
            writeIdleFuture.cancel(false);
            writeIdleFuture = null;
        }
        if (allIdleFuture != null) {
            allIdleFuture.cancel(false);
            allIdleFuture = null;
        }
        log.info("destroy finish!");

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        destroy();
        ctx.fireChannelInactive();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        reading = true;
        ctx.fireChannelRead(msg);
        log.info("ctx={};msg={};reading={};", ctx, msg, reading);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelReadComplete();
        reading = false;
        lastReadTime = System.currentTimeMillis();
        ctx.fireChannelReadComplete();
        log.info("ctx={};reading={};", ctx, reading);
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        ctx.fireUserEventTriggered(evt);
    }

    @Override
    public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {
        ctx.fireChannelWritabilityChanged();
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        if (ctx.channel().isOpen()) {
            initialize(ctx);
        }
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        destroy();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.fireExceptionCaught(cause);
    }

    @Override
    public void bind(ChannelHandlerContext ctx, SocketAddress localAddress, ChannelPromise promise) throws Exception {
        ctx.bind(localAddress, promise);
    }

    @Override
    public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) throws Exception {
        ctx.connect(remoteAddress, localAddress);
    }

    @Override
    public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        ctx.disconnect(promise);
        destroy();
    }

    @Override
    public void close(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        destroy();
        ctx.close(promise);
    }

    @Override
    public void deregister(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        destroy();
        ctx.deregister(promise);
    }

    @Override
    public void read(ChannelHandlerContext ctx) throws Exception {
        //reading = true;
        ctx.read();
        log.info("read!ctx={};", ctx);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        ctx.write(msg, promise).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                lastWriteTime = System.currentTimeMillis();
            }
        });
        log.info("write!ctx={};msg={};", ctx, msg);
    }

    @Override
    public void flush(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }


    abstract class AbstractTimerTask implements Runnable {
        private final ChannelHandlerContext ctx;

        public AbstractTimerTask(ChannelHandlerContext ctx) {
            this.ctx = ctx;

        }

        public void run() {
            if (this.ctx.channel().isOpen()) {
                run(this.ctx);
            }
        }

        public abstract void run(ChannelHandlerContext ctx);

    }

    final class ReadIdleTask extends AbstractTimerTask {
        public ReadIdleTask(ChannelHandlerContext ctx) {
            super(ctx);
        }

        /**
         * 检查一下是否到期;
         * 若没到期,定义下一个定时器;
         * 到期后要发送idleEvent,定义下一个定时器;
         *
         * @param ctx
         */
        @Override
        public void run(ChannelHandlerContext ctx) {
            final long nextDelay = (reading ? readIdleDuration : (readIdleDuration - (System.currentTimeMillis() - lastReadTime)));
            if (nextDelay <= 0) {
                //到期了;
                MyIdleStateEvent myIdleStateEvent = (firstReadIdle ? MyIdleStateEvent.FIRST_READER_IDLE_STATE_EVENT : MyIdleStateEvent.READER_IDLE_STATE_EVENT);
                firstReadIdle = false;
                ctx.fireUserEventTriggered(myIdleStateEvent);
                readIdleFuture = ctx.executor().schedule(this, readIdleDuration, TimeUnit.MILLISECONDS);
            } else {
                readIdleFuture = ctx.executor().schedule(this, nextDelay, TimeUnit.MILLISECONDS);
            }
            log.info("run finish!nextDelay={}ms;lastReadTime={}ms;reading={};", nextDelay, lastReadTime, reading);
        }
    }

    final class WriteIdleTask extends AbstractTimerTask {
        public WriteIdleTask(ChannelHandlerContext ctx) {
            super(ctx);
        }

        @Override
        public void run(ChannelHandlerContext ctx) {
            final long nextDelay = (writeIdleDuration - (System.currentTimeMillis() - lastWriteTime));
            if (nextDelay <= 0) {
                //到期了;
                MyIdleStateEvent myIdleStateEvent = (firstWriteIdle ? MyIdleStateEvent.FIRST_WRITER_IDLE_STATE_EVENT : MyIdleStateEvent.WRITER_IDLE_STATE_EVENT);
                firstWriteIdle = false;
                ctx.fireUserEventTriggered(myIdleStateEvent);
                writeIdleFuture = ctx.executor().schedule(this, writeIdleDuration, TimeUnit.MILLISECONDS);
            } else {
                writeIdleFuture = ctx.executor().schedule(this, nextDelay, TimeUnit.MILLISECONDS);
            }
            log.info("run finish!nextDelay={}ms;lastWriteTime={}ms;", nextDelay, lastWriteTime);
        }
    }

    final class AllIdleTask extends AbstractTimerTask {
        public AllIdleTask(ChannelHandlerContext ctx) {
            super(ctx);
        }

        @Override
        public void run(ChannelHandlerContext ctx) {
            final long nextDelay = (reading ? allIdleDuration : (allIdleDuration - (System.currentTimeMillis() - Math.max(lastReadTime, lastWriteTime))));

            if (nextDelay <= 0) {
                //到期了;
                MyIdleStateEvent myIdleStateEvent = (firstAllIdle ? MyIdleStateEvent.FIRST_ALL_IDLE_STATE_EVENT : MyIdleStateEvent.ALL_IDLE_STATE_EVENT);
                firstAllIdle = false;
                ctx.fireUserEventTriggered(myIdleStateEvent);
                allIdleFuture = ctx.executor().schedule(this, allIdleDuration, TimeUnit.MILLISECONDS);
            } else {
                //没到期;
                allIdleFuture = ctx.executor().schedule(this, nextDelay, TimeUnit.MILLISECONDS);
            }
            log.info("run finish!nextDelay={}ms;", nextDelay);

        }
    }


}
