package wjt.netty.c;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import wjt.netty.NettyConfig;

import javax.annotation.Resource;

import static org.junit.Assert.*;

/**
 mvn clean test -Dtest=wjt.netty.c.TimeClientTest#run

 mvn clean test -Dtest=wjt.netty.c.TimeServerTest#run
 */
@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {NettyConfig.class})
public class TimeServerTest {

    @Resource
    private TimeServer timeServer;

    @Test
    public void run() {

        timeServer.run();
        log.info("time server finish!timeServer={};", timeServer);

    }
}