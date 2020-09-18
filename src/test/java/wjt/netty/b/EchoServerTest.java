package wjt.netty.b;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import wjt.netty.NettyConfig;

import javax.annotation.Resource;

import static org.junit.Assert.*;


@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {NettyConfig.class})
public class EchoServerTest {

    @Resource
    private EchoServer echoServer;

    @Test
    public void run() {
        echoServer.run();
        log.info("echoServer finish!");
    }
}