package wjt.netty;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import wjt.netty.heart.HeartbeatServer;

@Slf4j
@Configuration
@ComponentScan(basePackageClasses = {HeartbeatServer.class})
public class HeartbeatConfig {
}
