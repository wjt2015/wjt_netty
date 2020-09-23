package wjt.netty.f;

import lombok.ToString;

import java.net.InetSocketAddress;

@ToString
public class MyUDPData {
    public final InetSocketAddress destAddr;
    public String content;

    public MyUDPData(InetSocketAddress destAddr, String content) {
        this.destAddr = destAddr;
        this.content = content;
    }
}
