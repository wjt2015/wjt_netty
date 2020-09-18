package wjt.netty.c;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MyMessageToByteEncoder<I> extends MessageToByteEncoder<MyData> {
    @Override
    protected void encode(ChannelHandlerContext ctx, MyData msg, ByteBuf out) throws Exception {

    }
}
