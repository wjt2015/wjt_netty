package wjt.netty.e;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;

import java.math.BigInteger;
import java.util.List;

@Slf4j
public class BigIntegerDecoder extends ByteToMessageDecoder {

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int readableBytes = in.readableBytes();
        log.info("ctx={};readableBytes={};in={};", ctx, readableBytes, in.toString(CharsetUtil.UTF_8));
        if (readableBytes < 5) {
            return;
        }
        in.markReaderIndex();

        final byte magicNum = in.readByte();
        //check the magic num;
        if (magicNum != Common.BIG_INT_PREFIX) {
            in.resetReaderIndex();
            throw new CorruptedFrameException("Invalid magic number:" + magicNum);
        }

        //wait until the whole data is available;
        int dataSize = in.readInt();
        log.info("dataSize={};readableBytes={};", dataSize, in.readableBytes());
        if (in.readableBytes() < dataSize) {
            in.resetReaderIndex();
            return;
        }
        //convert the recv data into a new bigInt;
        byte[] decoded = new byte[dataSize];
        in.readBytes(decoded);
        out.add(new BigInteger(decoded));
        log.info("bigInt decode finish!out={};", out);
    }
}
