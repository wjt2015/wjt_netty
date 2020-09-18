package wjt.netty.b;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import wjt.netty.NettyConfig;

import javax.annotation.Resource;

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {NettyConfig.class})
public class EchoClientTest {

    @Resource
    private EchoClient echoClient;

    @Test
    public void run() {
        echoClient.run();
        log.info("echoClient finish!");
    }
}