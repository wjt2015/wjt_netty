package wjt.netty.f;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MyMessageToMessageDecoder;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.List;

@Slf4j
public class UDPDecoder extends MyMessageToMessageDecoder<DatagramPacket> {

    @Override
    protected void decode(ChannelHandlerContext ctx, DatagramPacket msg, List<Object> out) throws Exception {
        ByteBuf content = msg.content();
        String str = content.toString(CharsetUtil.UTF_8);
        InetSocketAddress senderAddr = msg.sender();
        out.add(str);
        log.info("read;senderAddr={};content={};ctx={};out={};", senderAddr, str, ctx, out);
    }
}
