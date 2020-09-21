package wjt.netty.e;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import wjt.netty.FactorialConfig;

import javax.annotation.Resource;

import static org.junit.Assert.*;


/**
 mvn clean test -Dtest=wjt.netty.e.FactorialServerTest#run


 */
@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {FactorialConfig.class})
public class FactorialServerTest {

    @Resource
    private FactorialServer factorialServer;

    @Test
    public void run() {

        factorialServer.run();
        log.info("factorialServer finish!factorialServer={};",factorialServer);

    }
}