package wjt.netty.c;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class TimeClient {

    private String host="127.0.0.1";
    private int port=10003;

    public void run(){

        EventLoopGroup group=new NioEventLoopGroup();
        try {


        }catch (Exception e){
            log.error("time client error!",e);
        }finally {
            group.shutdownGracefully();
            log.info("time client  shutdown gracefully!");
        }

    }

}
