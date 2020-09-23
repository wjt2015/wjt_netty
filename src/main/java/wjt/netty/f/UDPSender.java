package wjt.netty.f;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

@Slf4j
public class UDPSender {
    private final int port;
    private final InetSocketAddress destAddr;

    public UDPSender(int port, InetSocketAddress destAddr) {
        this.port = port;
        this.destAddr = destAddr;
    }


    public void run() {
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();
        try {
            b.group(group)
                    .channel(NioDatagramChannel.class)
                    .option(ChannelOption.SO_BROADCAST, true)
                    .handler(new ChannelInitializer<NioDatagramChannel>() {
                        @Override
                        protected void initChannel(NioDatagramChannel ch) throws Exception {
                            ch.pipeline().addLast(new UDPDecoder()).addLast(new UDPEncoder())
                                    .addLast(new UDPOutHandler());
                        }
                    });

            ChannelFuture f = b.bind(port).sync();
            log.info("udp sender bind successfully!port={};", port);
            Channel channel = f.channel();

            sendMsg(channel);

            channel.closeFuture().sync();
            log.info("udp sender close!");
        } catch (Exception e) {
            log.info("udp sender error!", e);
        } finally {
            group.shutdownGracefully();
        }
    }

    private void sendMsg(Channel channel) {
        String[] msgs = new String[]{
                "《Netty 4.x 用户指南》中文翻译（包含了官方文档以及其他文章），并在原文的基础上，插入配图，图文并茂方便用户理解。至今为止，Netty 的最新版本为 Netty 4.1.49.Final(2020-4-22)。",
                "最近在做的项目，需要自己搭建一个socks代理。netty4.0附带了一个socks代理的样例，但是3.x就没有这个东西了，碰巧使用的又是3.7，就只能自己摸索并实现一遍，也算是对netty和socks协议的一个熟悉。 socks代理涉及到协议解析、server、client等功能，是一个比较复杂的网络程序，对于学习netty的使用也是非常好的例子。",
                "socks是在传输层之上的一层协议，主要功能是提供代理认证等功能。socks协议虽然是应用层协议(在TCP/IP4层协议栈里)，本身可以理解为一个信道，可以传输任何TCP/UDP内容。例如著名的科学上网软件就是基于socks协议，对通信内容进行加密实现的。"
        };

        for (String msg : msgs) {
            channel.write(new MyUDPData(destAddr, msg));
        }
        channel.flush();
        log.info("sendMsg finish!channel={};", channel);
    }


}
