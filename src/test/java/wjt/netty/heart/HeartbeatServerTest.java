package wjt.netty.heart;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import wjt.netty.HeartbeatConfig;

import javax.annotation.Resource;

import static org.junit.Assert.*;

/**
 mvn clean test -Dtest=wjt.netty.heart.HeartbeatServerTest#run
 */

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {HeartbeatConfig.class})
public class HeartbeatServerTest {
    @Resource
    private HeartbeatServer heartbeatServer;


    @Test
    public void run() {

        heartbeatServer.run();

        log.info("heartbeatServer={}; finish!", heartbeatServer);

    }
}