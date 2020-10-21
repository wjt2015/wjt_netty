package wjt.netty;

import io.netty.handler.timeout.MyIdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import wjt.netty.heart.HeartbeatServer;
import wjt.netty.heart.WjtIdleStateHandler;

import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
@EnableAspectJAutoProxy
@ComponentScan(basePackageClasses = {HeartbeatServer.class})
public class HeartbeatConfig {

    private static final int READ_IDLE_TIME_OUT = 15;
    private static final int WRITE_IDLE_TIME_OUT = 18;
    private static final int ALL_IDLE_TIME_OUT = 20;

    @Bean
    public MyIdleStateHandler myIdleStateHandler() {
        return new MyIdleStateHandler(true, READ_IDLE_TIME_OUT, WRITE_IDLE_TIME_OUT, ALL_IDLE_TIME_OUT, TimeUnit.SECONDS);
    }

    @Bean
    public WjtIdleStateHandler wjtIdleStateHandler() {
        return new WjtIdleStateHandler(READ_IDLE_TIME_OUT, WRITE_IDLE_TIME_OUT, ALL_IDLE_TIME_OUT);
    }

}
