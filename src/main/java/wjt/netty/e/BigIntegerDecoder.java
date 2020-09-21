package wjt.netty.e;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.List;

@Slf4j
@Service
public class BigIntegerDecoder extends ByteToMessageDecoder {

    private static final short BIG_INT_PREFIX = 'F';

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int readableBytes = in.readableBytes();
        log.info("readableBytes={};in={};", readableBytes, in.toString(CharsetUtil.UTF_8));
        if (readableBytes < 5) {
            return;
        }

        in.markReaderIndex();

        final short magicNum = in.readUnsignedByte();
        //check the magic num;
        if (magicNum != BIG_INT_PREFIX) {
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
    }
}
