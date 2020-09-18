package wjt.netty;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import wjt.netty.c.TimeServer;

@Configuration
@ComponentScan(basePackageClasses = {TimeServer.class})
public class NettyConfig {

}
