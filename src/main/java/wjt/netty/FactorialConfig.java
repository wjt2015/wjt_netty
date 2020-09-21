package wjt.netty;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import wjt.netty.e.FactorialServer;

@Configuration
@ComponentScan(basePackageClasses = FactorialServer.class)
public class FactorialConfig {
}
