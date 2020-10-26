package wjt.netty.httpfile;

import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * mvn clean test -Dtest=wjt.netty.httpfile.HttpFileServerTest#run
 */
@Slf4j
public class HttpFileServerTest {

    private HttpFileServer httpFileServer;

    @Before
    public void init() {
        httpFileServer = new HttpFileServer(10009);
    }

    @Test
    public void run() {
        httpFileServer.run();
        log.info("httpFileServer finish!");
    }
}