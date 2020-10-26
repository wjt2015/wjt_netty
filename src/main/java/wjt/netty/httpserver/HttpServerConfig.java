package wjt.netty.httpserver;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class HttpServerConfig {

    @Bean
    public HttpServer httpServer() {
        return new HttpServer(10009);
    }


}
