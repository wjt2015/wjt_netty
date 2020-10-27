package wjt.netty.httpfile;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.handler.stream.MyChunkedFile;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.io.RandomAccessFile;

import static org.junit.Assert.*;

/**
 * mvn clean test -Dtest=wjt.netty.httpfile.HttpFileServerTest#run
 * http://127.0.0.1:10009/java
 */
@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {HttpFileConfig.class})
public class HttpFileServerTest {

    @Resource
    private HttpFileServer httpFileServer;

    @Resource
    private LogAspect logAspect;

/*    @Before
    public void init() {
        httpFileServer = new HttpFileServer(10009);
    }*/

    @Test
    public void run() {
        log.info("logAspect={};", logAspect);


        httpFileServer.run();
        log.info("httpFileServer finish!");
    }

    /**
     * 研究ChunkedFile;
     */
    @Test
    public void chunkFile() {
        String fileName = "/Users/jintao9/linux2014/test/data/java/Java高级架构面试知识点整理.pdf";
        MyChunkedFile chunkedFile = null;
        ByteBuf byteBuf = null;
        final int bufSize = 1 << 12;
        final ByteBufAllocator byteBufAllocator = new UnpooledByteBufAllocator(true);
        final long start = System.currentTimeMillis();
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(fileName, "r")) {
            chunkedFile = new MyChunkedFile(randomAccessFile, bufSize);

            while (true) {
                byteBuf = chunkedFile.readChunk(byteBufAllocator);
                if (byteBuf == null) {
                    break;
                }
                log.info("read;byteBuf.size={};process={};", byteBuf.readableBytes(), chunkedFile.progress());
                if (byteBuf.readableBytes() < bufSize) {
                    break;
                }
            }

        } catch (Exception e) {
            log.error("read file error!fileName={};", fileName, e);
        } finally {
            log.info("chunkFile_finish!elapsed={}ms;", (System.currentTimeMillis() - start));
        }

    }
}