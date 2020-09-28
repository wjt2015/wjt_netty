package wjt.netty.file;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FileServer {
    private final int port;

    public FileServer(int port) {
        this.port = port;
    }

    public FileServer() {
        this(FileCommon.SERVER_PORT);
    }

    public void run(){
        EventLoopGroup bossGroup=new NioEventLoopGroup();
        EventLoopGroup workerGroup=new NioEventLoopGroup();
        ServerBootstrap b=new ServerBootstrap();
        try {

        }catch (Exception e){
            log.error("server error!",e);
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }



    }

}
