package wjt.netty;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import wjt.netty.f.Common;
import wjt.netty.f.UDPReceiver;
import wjt.netty.f.UDPSender;

import java.net.InetSocketAddress;

@Configuration
@ComponentScan(basePackageClasses = {UDPReceiver.class})
public class UDPConfig {

    @Bean
    public UDPSender udpSender() {
        return new UDPSender(Common.SENDER_PORT, new InetSocketAddress(Common.HOST, Common.RECEIVER_PORT));
    }

    @Bean
    public UDPReceiver udpReceiver() {
        return new UDPReceiver(Common.RECEIVER_PORT, new InetSocketAddress(Common.HOST, Common.SENDER_PORT));
    }
}
