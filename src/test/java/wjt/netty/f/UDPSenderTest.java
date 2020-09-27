package wjt.netty.f;

import com.huaban.analysis.jieba.JiebaSegmenter;
import com.medallia.word2vec.Word2VecModel;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import wjt.netty.UDPConfig;

import javax.annotation.Resource;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static org.junit.Assert.*;

/**
 mvn clean test -Dtest=wjt.netty.f.UDPSenderTest#run

 */

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {UDPConfig.class})
public class UDPSenderTest {

    @Resource
    private UDPSender udpSender;

    @Test
    public void run() {
        udpSender.run();
        log.info("udpSender finish!udpSender={};", udpSender);
    }


    @Test
    public void word2vec(){
        DateTime dateTime=new DateTime();
    }

    @Test
    public void iterate(){

        HashMap<String,String> hashMap=new HashMap<>();

        Iterator<Map.Entry<String, String>> iterator = hashMap.entrySet().iterator();


    }


}