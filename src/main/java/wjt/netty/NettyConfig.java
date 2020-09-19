package wjt.netty;

import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import wjt.netty.d.TelnetServer;

@Configuration
@ComponentScan(basePackageClasses = {TelnetServer.class})
public class NettyConfig {

    @Bean
    public StringEncoder stringEncoder() {
        return new StringEncoder();
    }

    @Bean
    public StringDecoder stringDecoder() {
        return new StringDecoder();
    }

    @Bean
    public DelimiterBasedFrameDecoder delimiterBasedFrameDecoder() {
        return new DelimiterBasedFrameDecoder(8192, Delimiters.lineDelimiter());
    }

}
