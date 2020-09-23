package wjt.netty.f;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import wjt.netty.UDPConfig;

import javax.annotation.Resource;

/**
 mvn clean test -Dtest=wjt.netty.f.UDPReceiverTest#run

 */
@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {UDPConfig.class})
public class UDPReceiverTest {

    @Resource
    private UDPReceiver udpReceiver;

    @Test
    public void run() {
        udpReceiver.run();
        log.info("udpReceiver finish!udpReceiver={};", udpReceiver);
    }
}