package wjt.netty.barrage;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 {
 20201217,
 基于netty构建弹幕系统:
 https://v.qq.com/x/page/o0861bf7bjs.html
 }
 */
public class BarrageServer {

    public void run(){

        ServerBootstrap b=new ServerBootstrap();

        EventLoopGroup boss=new NioEventLoopGroup(1);
        EventLoopGroup worker=new NioEventLoopGroup(8);

        b.group(boss,worker)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {

                    }
                });


    }
}
