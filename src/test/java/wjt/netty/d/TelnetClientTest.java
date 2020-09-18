package wjt.netty.d;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import wjt.netty.NettyConfig;

import javax.annotation.Resource;

import static org.junit.Assert.*;

/**
 mvn clean test -Dtest=wjt.netty.d.TelnetClientTest#run

 */
@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {NettyConfig.class})
public class TelnetClientTest {

    @Resource
    private TelnetClient telnetClient;

    @Test
    public void run() {
        telnetClient.run();
        log.info("telnetClient finish!");
    }
}