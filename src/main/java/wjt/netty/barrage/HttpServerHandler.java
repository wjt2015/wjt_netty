package wjt.netty.barrage;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.DecoderResult;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpContent;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class HttpServerHandler extends SimpleChannelInboundHandler {

    private AtomicInteger n = new AtomicInteger(0);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {

        n.incrementAndGet();
        log.info("n={};msg.Class={};msg={};", n.get(), msg.getClass(), msg);

        if (msg instanceof DefaultHttpRequest) {
            DefaultHttpRequest httpRequest = (DefaultHttpRequest) msg;
            log.info("httpRequest={};", httpRequest);
        } else if (msg instanceof DefaultHttpContent) {
            DefaultHttpContent httpContent = (DefaultHttpContent) msg;
            log.info("httpContent={};", httpContent.content().toString(StandardCharsets.UTF_8));
        } else if (msg instanceof LastHttpContent) {
            log.info("lastHttpContent!n={};", n.get());
            n = new AtomicInteger(0);
            log.info("msg.Class={};msg={};", msg.getClass(), msg);
            DefaultFullHttpResponse httpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
            httpResponse.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html;charset=utf-8");
            httpResponse.content().writeBytes("netty弹幕服务器测试!".getBytes());
            ctx.writeAndFlush(httpResponse).addListener(ChannelFutureListener.CLOSE);
        } else {
            log.info("msg other type!");
        }

    }
}
