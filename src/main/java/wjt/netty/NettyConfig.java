package wjt.netty;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import wjt.netty.a.DiscardServer;

@Configuration
@ComponentScan(basePackageClasses = {DiscardServer.class})
public class NettyConfig {

}
