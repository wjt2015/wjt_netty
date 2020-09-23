package wjt.common;

import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AuxUtils {

    public static String byteBuf(final ByteBuf byteBuf) {
        byteBuf.markReaderIndex();
        int bytes = byteBuf.readableBytes();
        byte[] data = new byte[bytes];

        byteBuf.readBytes(data);

        byteBuf.resetReaderIndex();

        log.info("byteBuf_data={};", data);

        StringBuilder stringBuilder = new StringBuilder().append("[");
        int i = 0, size = data.length;
        for (byte v : data) {
            stringBuilder.append(v);
            i++;
            if (i < size) {
                stringBuilder.append(",");
            }else {
                stringBuilder.append("]");
            }
        }
        return stringBuilder.toString();
    }

}
