package wjt.netty.d;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import wjt.netty.NettyConfig;

import javax.annotation.Resource;

import java.math.BigInteger;

import static org.junit.Assert.*;

/**
 * mvn clean test -Dtest=wjt.netty.d.TelnetServerTest#run
 */
@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {NettyConfig.class})
public class TelnetServerTest {

    @Resource
    private TelnetServer telnetServer;

    @Test
    public void run() {

        telnetServer.run();
        log.info("telnetServer finish!");

    }


    @Test
    public void bigInt() {
        byte[] data = new byte[]{0x11, 0x12, 0x34, 0x56, 0x78};
        BigInteger bigInt = new BigInteger(data);

        log.info("bigInt={};", bigInt);

        ByteBuf byteBuf = Unpooled.buffer(2);

        log.info("rIdx={};wIdx={};", byteBuf.readerIndex(), byteBuf.writerIndex());
        byteBuf.writeBytes(data);
        log.info("rIdx={};wIdx={};", byteBuf.readerIndex(), byteBuf.writerIndex());
        byte[] out = new byte[2];
        byteBuf.readBytes(out);
        log.info("rIdx={};wIdx={};out={};", byteBuf.readerIndex(), byteBuf.writerIndex(), out);

        byteBuf.markReaderIndex();
        byte[] out2=new byte[2];

        byteBuf.readBytes(out2);

        byteBuf.resetReaderIndex();
        short v = byteBuf.readShort();
        log.info("rIdx={};wIdx={};out2={};v={};", byteBuf.readerIndex(), byteBuf.writerIndex(),out2, v);

    }
}