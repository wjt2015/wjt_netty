package wjt.netty;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import wjt.netty.d.TelnetServer;

@Configuration
@ComponentScan(basePackageClasses = {TelnetServer.class})
public class NettyConfig {

}
