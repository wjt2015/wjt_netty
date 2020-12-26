package wjt.netty.barrage;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * {
 * 20201217,
 * 基于netty构建弹幕系统:
 * https://v.qq.com/x/page/o0861bf7bjs.html
 * }
 */
@Slf4j
@AllArgsConstructor
public class BarrageServer {

    private int port;


    public void run() {

        ServerBootstrap b = new ServerBootstrap();

        EventLoopGroup boss = new NioEventLoopGroup(1);
        EventLoopGroup worker = new NioEventLoopGroup(8);

        try {
            b.group(boss, worker)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<NioSocketChannel>() {
                        @Override
                        protected void initChannel(NioSocketChannel ch) throws Exception {
                            ch.pipeline().addLast("http_decoder", new HttpRequestDecoder())
                                    .addLast("http_encoder", new HttpResponseEncoder())
                                    .addLast("httpServlet", new HttpServerHandler());
                        }
                    });
            ChannelFuture f = b.bind(port).sync();
            log.info("server is running on port:{}!", port);
            f.channel().closeFuture().sync();
            log.info("server finish!port={};", port);
        } catch (Exception e) {
            log.error("channel error!", e);
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
        }


    }
}
