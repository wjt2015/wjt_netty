package wjt.netty.f;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.MyMessageToMessageEncoder;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class UDPEncoder extends MyMessageToMessageEncoder<MyUDPData> {

    @Override
    protected void encode(ChannelHandlerContext ctx, MyUDPData udpData, List<Object> out) throws Exception {
        ByteBuf byteBuf = ctx.alloc().buffer().writeBytes(udpData.content.getBytes(CharsetUtil.UTF_8));
        DatagramPacket datagramPacket = new DatagramPacket(byteBuf, udpData.destAddr);
        out.add(datagramPacket);
        log.info("write;ctx={};udpData={};datagramPacket={};out={};", ctx, udpData, datagramPacket, out);
    }
}
