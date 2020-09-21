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
 mvn clean test -Dtest=wjt.netty.e.FactorialClientTest#run

 */
@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {FactorialConfig.class})
public class FactorialClientTest {

    @Resource
    private FactorialClient factorialClient;

    @Test
    public void run() {

        factorialClient.run();
        log.info("factorialClient finish!factorialClient={};", factorialClient);

    }
}