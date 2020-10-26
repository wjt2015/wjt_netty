package wjt.netty.httpserver;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HttpServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
        final long start = System.currentTimeMillis();
        log.info("channelRead0 start:");
        readRequest(msg);
        final String sendMsg, uri = msg.uri();
        switch (uri) {
            case "/":
                sendMsg = "<h3>Netty HTTP Server</h3><p>Welcome to <a href=\"https://waylau.com\">waylau.com</a>!</p>";
                break;
            case "/hi":
                sendMsg = "<h3>Netty HTTP Server</h3><p>Hello Word!</p>";
                break;
            case "/love":
                sendMsg = "<h3>Netty HTTP Server</h3><p>I Love You!</p>";
                break;
            default:
                sendMsg = "<h3>Netty HTTP Server</h3><p>I was lost!</p>";
                break;
        }

        this.writeResponse(ctx, sendMsg);
        log.info("channelRead0 finish!elapsed={}ms;", (System.currentTimeMillis() - start));
    }

    private void readRequest(FullHttpRequest msg) {
        log.info("======请求行======");
        log.info("{} {} {}", msg.method(), msg.uri(), msg.protocolVersion());

        log.info("======请求头======");
        msg.headers().forEach(e -> {
            log.info("{}:{}", e.getKey(), e.getValue());
        });

        log.info("======消息体======");
        log.info("{}", msg.content().toString(CharsetUtil.UTF_8));
    }

    private void writeResponse(ChannelHandlerContext ctx, final String msg) {
        ByteBuf byteBuf = Unpooled.copiedBuffer(msg, CharsetUtil.UTF_8);
        DefaultFullHttpResponse fullHttpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, byteBuf);
        fullHttpResponse.headers().set(HttpHeaderNames.CONTENT_LENGTH, msg.length());
        ctx.writeAndFlush(fullHttpResponse).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
    }
}
