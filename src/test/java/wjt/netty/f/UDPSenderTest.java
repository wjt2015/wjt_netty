package wjt.netty.f;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import wjt.netty.UDPConfig;

import javax.annotation.Resource;

import static org.junit.Assert.*;

/**
 mvn clean test -Dtest=wjt.netty.f.UDPSenderTest#run

 */

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {UDPConfig.class})
public class UDPSenderTest {

    @Resource
    private UDPSender udpSender;

    @Test
    public void run() {
        udpSender.run();
        log.info("udpSender finish!udpSender={};", udpSender);
    }
}