package wjt.netty.barrage;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import static org.junit.Assert.*;

/**上传文件:
 * curl -F 'name=@curl.txt' http://localhost:10009/abc.json
 *
 */
@Slf4j
public class BarrageServerTest {


    @Test
    public void run() {

        final int port = 10009;
        BarrageServer barrageServer = new BarrageServer(port);
        barrageServer.run();
        log.info("barrageServer finish!");
    }
}