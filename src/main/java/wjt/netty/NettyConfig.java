package wjt.netty;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import wjt.netty.b.EchoServer;

@Configuration
@ComponentScan(basePackageClasses = {EchoServer.class})
public class NettyConfig {

}
