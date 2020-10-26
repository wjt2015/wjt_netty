package wjt.netty.httpserver;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.buffer.UnpooledByteBufAllocator;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import sun.net.www.protocol.http.HttpURLConnection;

import javax.annotation.Resource;
import java.net.MalformedURLException;
import java.net.URL;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {HttpServerConfig.class})
public class HttpServerConfigTest {

    @Resource
    private HttpServer httpServer;

    /**
     * mvn clean test -Dtest=wjt.netty.httpserver.HttpServerConfigTest#httpServer
     * <p>
     * curl --header '' --cookie '' --data ''  http://127.0.0.1:10009/ssm_train/update.json
     * <p>
     * curl --header 'User-Agent: Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_6) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.80 Safari/537.36' --cookie 'traceid=ecf95c136c; domain=.cgi.connect.qq.com; path=/ secure' --data 'a=b&b=c'  http://127.0.0.1:10009/ssm_train/update.json
     */
    @Test
    public void httpServer() {
        httpServer.run();
        log.info("httpServer finish!");
    }


    @Test
    public void byteBuf() {

        final ByteBuf byteBuf = Unpooled.directBuffer(100);
        byteBuf.retain();
        log.info("byteBuf={};maxCapacity={};refCnt={};hasByteArray={};", byteBuf, byteBuf.maxCapacity(), byteBuf.refCnt(), byteBuf.hasArray());

        final ByteBuf src = Unpooled.directBuffer(50);

        src.writeBytes(new byte[]{1, 2, 3, 4, 5});

        byteBuf.writeBytes(src);

        final byte[] out = new byte[3];
        byteBuf.readBytes(out);

        byteBuf.release(1);
        log.info("byteBuf={};maxCapacity={};refCnt={};out={};", byteBuf, byteBuf.maxCapacity(), byteBuf.refCnt(), out);


        UnpooledByteBufAllocator bufAllocator = new UnpooledByteBufAllocator(true);

        ByteBuf byteBufA = bufAllocator.buffer(20).writeBytes(new byte[]{1, 2, 3, 4, 5});
        ByteBuf byteBufB = bufAllocator.buffer(20).writeBytes(new byte[]{1, 2, 3, 4, 5, 6, 7});

        CompositeByteBuf compositeByteBuf = bufAllocator.compositeDirectBuffer(10);
        compositeByteBuf.addComponent(true, byteBufA).addComponent(true, byteBufB);
        log.info("compositeByteBuf={};refCnt={};", compositeByteBuf, compositeByteBuf.refCnt());

        final byte[] outB = new byte[6];
        compositeByteBuf.readBytes(outB);
        log.info("compositeByteBuf={};refCnt={};outB={};", compositeByteBuf, compositeByteBuf.refCnt(), outB);


    }

    /**
     * 文件上传;
     */
    @Test
    public void upload() throws Exception {


    }

}