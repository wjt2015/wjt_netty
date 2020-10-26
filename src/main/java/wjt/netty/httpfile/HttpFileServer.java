package wjt.netty.httpfile;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.MyHttpObjectAggregator;
import io.netty.handler.stream.ChunkedWriteHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * http文件下载;
 * 参考,(https://blog.csdn.net/shengqianfeng/article/details/80849575);
 */
@Slf4j
public class HttpFileServer {
    private final int port;
    private String url;

    public static final String DEFAULT_URL = "/Users/jintao9/linux2014/test/data";
    public static final String DEFAULT_DIR = "/Users/jintao9/linux2014/test/data/";

    public HttpFileServer(int port, String url) {
        this.port = port;
        this.url = url;

        System.setProperty("user.dir", DEFAULT_DIR);
    }

    public HttpFileServer(int port) {
        this(port, DEFAULT_URL);
    }

    public void run() {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {

            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            //加入http解码器;
                            ch.pipeline().addLast("http_decoder", new HttpRequestDecoder())
                                    //将多个消息转换为单一的FullHttpRequest或FullHttpResponse;
                                    .addLast("http_aggregator", new MyHttpObjectAggregator(1 << 16))
                                    //http编码器;
                                    .addLast("http_encoder", new HttpResponseEncoder())
                                    //支持异步发送码流(大文件传输),但不占用过多的内存;
                                    .addLast("http_chunked", new ChunkedWriteHandler())
                                    //加入自定义的业务逻辑;
                                    .addLast("file_server_handler", new HttpFileServerHandler(url));
                        }
                    });

            ChannelFuture f = b.bind(port).sync();
            log.info("http_file_server bind on port={};", port);
            f.channel().closeFuture().sync();

        } catch (Exception e) {
            log.error("httpFileServer error!", e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

}