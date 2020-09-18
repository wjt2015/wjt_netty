package wjt.netty.a;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;


@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {DiscardServer.class})
public class DiscardServerTest {

    @Resource
    private DiscardServer discardServer;

    @Test
    public void run() {
        discardServer.run();

        log.info("the discard server is running!");

    }

}