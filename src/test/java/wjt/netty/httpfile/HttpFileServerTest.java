package wjt.netty.httpfile;

import ch.qos.logback.core.rolling.helper.FileNamePattern;
import ch.qos.logback.core.rolling.helper.PeriodicityType;
import ch.qos.logback.core.rolling.helper.RollingCalendar;
import ch.qos.logback.core.util.CachingDateFormatter;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.UnpooledByteBufAllocator;
import io.netty.handler.stream.MyChunkedFile;
import lombok.extern.slf4j.Slf4j;
import org.apache.log4j.pattern.CachedDateFormat;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import wjt.common.AuxUtils;

import javax.annotation.Resource;
import java.io.File;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * mvn clean test -Dtest=wjt.netty.httpfile.HttpFileServerTest#run
 * http://127.0.0.1:10009/java
 */
//@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {HttpFileConfig.class})
public class HttpFileServerTest {

    private static final Logger log = LoggerFactory.getLogger(HttpFileServerTest.class);

    @Resource
    private HttpFileServer httpFileServer;

    @Resource
    private LogAspect logAspect;

/*    @Before
    public void init() {
        httpFileServer = new HttpFileServer(10009);
    }*/

    @Test
    public void run() {
        log.info("logAspect={};", logAspect);


        httpFileServer.run();
        log.info("httpFileServer finish!");
    }

    /**
     * 研究ChunkedFile;
     */
    @Test
    public void chunkFile() {
        String fileName = "/Users/jintao9/linux2014/test/data/java/Java高级架构面试知识点整理.pdf";
        MyChunkedFile chunkedFile = null;
        ByteBuf byteBuf = null;
        final int bufSize = 1 << 12;
        final ByteBufAllocator byteBufAllocator = new UnpooledByteBufAllocator(true);
        final long start = System.currentTimeMillis();
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(fileName, "r")) {
            chunkedFile = new MyChunkedFile(randomAccessFile, bufSize);

            while (true) {
                byteBuf = chunkedFile.readChunk(byteBufAllocator);
                if (byteBuf == null) {
                    break;
                }
                log.info("read;byteBuf.size={};process={};", byteBuf.readableBytes(), chunkedFile.progress());
                if (byteBuf.readableBytes() < bufSize) {
                    break;
                }
            }

        } catch (Exception e) {
            log.error("read file error!fileName={};", fileName, e);
        } finally {
            log.info("chunkFile_finish!elapsed={}ms;", (System.currentTimeMillis() - start));
        }

    }


    private static final String ROCKETMQ_TOPIC = "rmq-group";
    //private static final String NAME_SRV_ADDR="192.168.138.187:9876;192.168.138.188:9876";
    //private static final String NAME_SRV_ADDR = "10.222.101.30:9876";
    private static final String NAME_SRV_ADDR = "127.0.0.1:9876";

    /**
     * 测试rocketmq;
     * 参考,(https://www.cnblogs.com/mayuan01/p/12391507.html);
     * (http://rocketmq.apache.org/docs/quick-start/);
     * mvn clean test -Dtest=wjt.netty.httpfile.HttpFileServerTest#rocketmqProducer
     */
    @Test
    public void rocketmqProducer() {
        DefaultMQProducer mqProducer = new DefaultMQProducer(ROCKETMQ_TOPIC);
        mqProducer.setVipChannelEnabled(false);
        mqProducer.setSendMsgTimeout(10000);
        mqProducer.setNamesrvAddr(NAME_SRV_ADDR);
        mqProducer.setInstanceName("mqProducer");


        try {
            mqProducer.start();
            for (int i = 0, n = 10; i < n; i++) {
                AuxUtils.sleep(1000);
                Message message = new Message("it_topic", "tagA", ("it_topic" + i).getBytes());
                SendResult sendResult = mqProducer.send(message);
                log.info("sendResult={};", sendResult);
            }
        } catch (Exception e) {
            log.error("rocketmq producer error!", e);
        } finally {
            mqProducer.shutdown();
        }
    }

    /**
     * mvn clean test -Dtest=wjt.netty.httpfile.HttpFileServerTest#rocketmqConsumer
     */
    @Test
    public void rocketmqConsumer() {
        DefaultMQPushConsumer mqPushConsumer = new DefaultMQPushConsumer(ROCKETMQ_TOPIC);
        mqPushConsumer.setNamesrvAddr(NAME_SRV_ADDR);
        mqPushConsumer.setInstanceName("consumer");
        mqPushConsumer.setVipChannelEnabled(false);
        mqPushConsumer.setSuspendCurrentQueueTimeMillis(10000);
        //mqPushConsumer.setConsumeTimeout(10000);

        try {
            mqPushConsumer.subscribe(ROCKETMQ_TOPIC, "tagA");
            mqPushConsumer.registerMessageListener(new MessageListenerConcurrently() {
                @Override
                public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                    log.info("msgs={};", msgs);
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                }
            });
            mqPushConsumer.start();
        } catch (Exception e) {
            log.error("rocketmq consumer error!", e);
        } finally {
            //mqPushConsumer.shutdown();
        }

        log.info("mqPushConsumer started ... ");
    }


    @Test
    public void testLogbackA() {
        int id = 1, n = 1000;

        while (id++ < n) {

            AuxUtils.sleep(500);
            log.error("test logback;id={};date={};", id, new Date().toString());
        }

        id = 2;
        //log.info("test logback;id={};date={};", id, new Date().toString());
    }

    @Test
    public void fileNamePattern() {

        //new FileNamePattern()

    }

    @Test
    public void timeZone() {

        TimeZone timeZone = TimeZone.getDefault();

        log.error("timeZone={};", timeZone);

        Calendar calendar = Calendar.getInstance();

        int zoneOffset = calendar.get(Calendar.ZONE_OFFSET);
        int dstOffset = calendar.get(Calendar.DST_OFFSET);

        log.error("calendar={};", calendar);
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        int millis = calendar.get(Calendar.MILLISECOND);

        log.error("year={};month={};dayOfMonth={};hourOfDay={};minute={};second={};millis={};zoneOffset={};dstOffset={};",
                year, month, dayOfMonth, hourOfDay, minute, second, millis, zoneOffset, dstOffset);

        calendar.add(Calendar.SECOND, 1);
        calendar.set(Calendar.MILLISECOND, 0);

        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);
        second = calendar.get(Calendar.SECOND);
        millis = calendar.get(Calendar.MILLISECOND);

        log.error("year={};month={};dayOfMonth={};hourOfDay={};minute={};second={};millis={};", year, month, dayOfMonth, hourOfDay, minute, second, millis);


    }

    private static final String DATE_PATTERN = "yyyy_MM_dd HH:mm:ss.SSS";

    @Test
    public void rollingCalendar() {
        String datePattern = "YYYY_MM_dd_HH";

        SimpleDateFormat sdf = new SimpleDateFormat(datePattern);
        log.error("sdf_date={};", sdf.format(new Date()));

        RollingCalendar rollingCalendar = new RollingCalendar(datePattern);
        PeriodicityType periodicityType = rollingCalendar.computePeriodicityType();
        Date endOfNextNthPeriod = rollingCalendar.getEndOfNextNthPeriod(new Date(), 1);

        Date nextTriggeringDate = rollingCalendar.getNextTriggeringDate(new Date());

        long startOfCurrentPeriodWithGMTOffsetCorrection = rollingCalendar.getStartOfCurrentPeriodWithGMTOffsetCorrection(System.currentTimeMillis(), TimeZone.getDefault());

        boolean collisionFree = rollingCalendar.isCollisionFree();

        long periodBarriersCrossed = rollingCalendar.periodBarriersCrossed(System.currentTimeMillis() - 10000, System.currentTimeMillis() - 1000);

        log.error("rollingCalendar={};periodicityType={};endOfNextNthPeriod={};nextTriggeringDate={};startOfCurrentPeriodWithGMTOffsetCorrection={};collisionFree={};periodBarriersCrossed={};",
                rollingCalendar, periodicityType, endOfNextNthPeriod, nextTriggeringDate, startOfCurrentPeriodWithGMTOffsetCorrection, collisionFree, periodBarriersCrossed);


        TimeZone gmtTimeZone = TimeZone.getTimeZone("GMT");
        TimeZone gmtE8TimeZone = TimeZone.getTimeZone("GMT-8");
        TimeZone gmtW8TimeZone = TimeZone.getTimeZone("GMT+8");

        log.error("gmtTimeZone={};gmtE8TimeZone={};gmtW8TimeZone={};", gmtTimeZone, gmtE8TimeZone, gmtW8TimeZone);

        Date now = new Date();
        nextTriggeringDate = rollingCalendar.getNextTriggeringDate(now);
        log.error("nextTriggeringDate={};dateTime={};", nextTriggeringDate, new DateTime(nextTriggeringDate.getTime()).toString(DATE_PATTERN));

        Date endOfNextNthPeriod0 = rollingCalendar.getEndOfNextNthPeriod(now, 0);
        Date endOfNextNthPeriod1 = rollingCalendar.getEndOfNextNthPeriod(now, 1);
        log.error("now_dt={};\nendOfNextNthPeriod0_dt={};\nendOfNextNthPeriod1_dt={};",
                new DateTime(now.getTime()).toString(DATE_PATTERN),
                new DateTime(endOfNextNthPeriod0.getTime()).toString(DATE_PATTERN),
                new DateTime(endOfNextNthPeriod1.getTime()).toString(DATE_PATTERN));
        Calendar calendar = Calendar.getInstance();

        long startOfCurrentPeriod = rollingCalendar.getStartOfCurrentPeriodWithGMTOffsetCorrection(now.getTime(), TimeZone.getDefault());

        log.error("startOfCurrentPeriod_dt={};", new DateTime(startOfCurrentPeriod).toString(DATE_PATTERN));

        int zoneOffset = calendar.get(Calendar.ZONE_OFFSET);
        int dstOffset = calendar.get(Calendar.DST_OFFSET);
        log.error("zoneOffset={};dstOffset={};", zoneOffset, dstOffset);


    }

    @Test
    public void startOfCurrentPeriod() {
        //String datePattern = "YYYY_MM_dd_HH";
        String datePattern = "YYYY_MM_dd";
        RollingCalendar rollingCalendar = new RollingCalendar(datePattern);

        long startOfCurrentPeriodWithGMTOffsetCorrection = rollingCalendar.getStartOfCurrentPeriodWithGMTOffsetCorrection(System.currentTimeMillis(), TimeZone.getDefault());

        log.error("startOfCurrentPeriodWithGMTOffsetCorrection_dt={};", new DateTime(startOfCurrentPeriodWithGMTOffsetCorrection).toString(DATE_PATTERN));

    }

    @Test
    public void  cachingDateFormatter(){
        String datePattern="yyyy_MM_dd-HH";
        CachingDateFormatter cachingDateFormatter = new CachingDateFormatter(datePattern);

        String format = cachingDateFormatter.format(System.currentTimeMillis());

        log.error("format={};",format);


    }

    @Test
    public void dateTime() {

        Calendar calendar = Calendar.getInstance();

        log.info("calendar={};", calendar);


        DateTime dateTimeA = new DateTime();
        DateTime dateTimeB = new DateTime(System.currentTimeMillis() - 100000);


        long currentTimeMillis = System.currentTimeMillis();

        long hour = currentTimeMillis / (3600 * 1000);
        long leftMillis = currentTimeMillis % (3600 * 1000);

        log.error("hour={};leftMillis={};", hour, leftMillis);

    }

}