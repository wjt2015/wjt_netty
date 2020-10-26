package wjt.netty.httpserver;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HttpServerHandlerInitializer extends ChannelInitializer<SocketChannel> {


    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addLast("codec", new HttpServerCodec())
                .addLast("aggregator", new HttpObjectAggregator(1048576))
                .addLast("serverHandler", new HttpServerHandler());

    }
}
