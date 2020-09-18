package wjt.netty.a;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import wjt.netty.NettyConfig;

import javax.annotation.Resource;

import static org.junit.Assert.*;

/**
 mvn clean test -Dtest=wjt.netty.a.DiscardClientTest#run

 mvn clean test -Dtest=wjt.netty.a.DiscardServerTest#run
 */
@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {NettyConfig.class})
public class DiscardClientTest {

    @Resource
    private DiscardClient discardClient;

    @Test
    public void run() {
        discardClient.run();
        log.info("discardClient finish running!");
    }
}


