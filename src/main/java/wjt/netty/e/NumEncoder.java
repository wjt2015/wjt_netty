package wjt.netty.e;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigInteger;

@Slf4j
@Service
@ChannelHandler.Sharable
public class NumEncoder extends MessageToByteEncoder<Number> {

    public static final byte BIG_INT_PREFIX = (byte) ('F');

    @Override
    protected void encode(ChannelHandlerContext ctx, Number msg, ByteBuf out) throws Exception {
        BigInteger v = null;
        if (msg instanceof BigInteger) {
            v = (BigInteger) msg;
        } else {
            v = new BigInteger(String.valueOf(msg));
        }

        byte[] data = v.toByteArray();

        out.writeByte(BIG_INT_PREFIX);
        out.writeInt(data.length);
        out.writeBytes(data);
        log.info("num encode finish!ctx={};out={};", ctx, out.toString(CharsetUtil.UTF_8));
    }
}
