package wjt.netty.httpfile;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@Slf4j
@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = true)
@ComponentScan(basePackageClasses = {HttpFileServer.class},
        excludeFilters = {@ComponentScan.Filter(classes = {Configuration.class})}
/*includeFilters = {@ComponentScan.Filter(classes = {HttpFileServer.class, LogAspect.class})}*/)
public class HttpFileConfig {

    @Bean
    public HttpFileServerHandler httpFileServerHandler() {
        return new HttpFileServerHandler(HttpFileServer.DEFAULT_URL);
    }
}
