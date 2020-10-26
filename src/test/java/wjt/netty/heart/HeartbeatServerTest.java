package wjt.netty.heart;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import wjt.common.AuxUtils;
import wjt.netty.HeartbeatConfig;

import javax.annotation.Resource;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * mvn clean test -Dtest=wjt.netty.heart.HeartbeatServerTest#run
 */

@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {HeartbeatConfig.class})
public class HeartbeatServerTest {
    @Resource
    private HeartbeatServer heartbeatServer;


    @Test
    public void run() {

        heartbeatServer.run();

        log.info("heartbeatServer={}; finish!", heartbeatServer);

    }

    private ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(1);


    /**
     * 基于延迟任务的定时器;
     */
    @Test
    public void schedule() {
        final int maxCount = 10;
        final long delay=50;
        final CountDownLatch countDownLatch = new CountDownLatch(maxCount);
        scheduledExecutorService.schedule(new TimedTask(scheduledExecutorService, 1L, 1, maxCount, countDownLatch,delay), delay, TimeUnit.MILLISECONDS);

        AuxUtils.await(countDownLatch, -1L);
        log.info("schedule_finish!");
    }

    static class TimedTask implements Runnable {
        private final ScheduledExecutorService scheduledExecutorService;
        private long taskId;
        private int count;
        private final int maxCount;
        private CountDownLatch countDownLatch;
        /**
         * millis;
         */
        private long delay;

        public TimedTask(ScheduledExecutorService scheduledExecutorService, long taskId, int count, int maxCount, CountDownLatch countDownLatch, long delay) {
            this.scheduledExecutorService = scheduledExecutorService;
            this.taskId = taskId;
            this.count = count;
            this.maxCount = maxCount;
            this.countDownLatch = countDownLatch;
            this.delay = delay;
        }

        @Override
        public void run() {
            log.info("doTask!taskId={};count={};maxCount={};", taskId, count, maxCount);
            countDownLatch.countDown();
            if (count < maxCount) {
                this.count++;
                this.scheduledExecutorService.schedule(this, delay, TimeUnit.MILLISECONDS);
            }
        }
    }


}